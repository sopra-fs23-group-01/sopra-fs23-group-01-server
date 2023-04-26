package ch.uzh.ifi.hase.soprafs23.controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import ch.uzh.ifi.hase.soprafs23.service.ChatService;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {
    @Lazy
    private final RoomService roomService;
    @Lazy
    private final UserService userService;


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    @Lazy
    private ChatService chatService;

    private Map<String, String> userWordMap = new ConcurrentHashMap<>();

    public ChatController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message) {
        // 如果是加入房间的消息，那么发送给他们分配的单词
        if (message.getStatus() == Status.JOIN) {
            chatService.userJoin(message.getSenderName());
            String assignedWord = userWordMap.get(message.getSenderName());
            String assignedRole = chatService.assignUserRole();
            Message wordMessage = new Message();
            wordMessage.setSenderName("system");
            wordMessage.setMessage(assignedWord);
            wordMessage.setStatus(Status.ASSIGNED_WORD); 
            wordMessage.setRole(assignedRole); // 修改状态为 ASSIGNED_WORD
            simpMessagingTemplate.convertAndSendToUser(message.getSenderName(), "/private", wordMessage);
        }
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        System.out.println(message.toString());
        return message;
    }

    @MessageMapping(value = "/gamestart/{roomId}")
    public void startGame(@PathVariable Long roomId) {
        Room room = roomService.findRoomById(roomId);
        if (roomService.checkIfAllReady(room)) {
            chatService.broadcastGameStart();
            chatService.initiateGame(room);
            while(!room.getGameStage().equals(GameStage.END)){chatService.conductTurn(room);}
            chatService.broadcastGameEnd(room);


        }else {
            chatService.systemReminder("Not enough players or not all players are ready yet!");
        }
    }
}
