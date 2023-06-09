package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import ch.uzh.ifi.hase.soprafs23.service.ChatService;

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

    public ChatController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    //mapping to the message sent from the room
    @MessageMapping("/message/{roomId}")
    @SendTo("/chatroom/{roomId}/public")
    public Message receiveMessage(@Payload Message message,@DestinationVariable("roomId") Long roomId) {
        if (message.getStatus() == Status.ASSIGNED_WORD) {
            String word = roomService.assignWord(message.getSenderName());
            String side = roomService.assignSide(message.getSenderName());
            Message wordMessage = new Message();
            wordMessage.setSenderName("system");
            wordMessage.setStatus(Status.ASSIGNED_WORD);
            wordMessage.setRole(word);
            wordMessage.setMessage(side);
            simpMessagingTemplate.convertAndSendToUser(message.getSenderName(), "/private", wordMessage);
        }
        
        return message;
    }

    @MessageMapping("/roomcreat")
    @SendTo("/room")
    public Message receiveCreationMessage(@Payload Message message) {
        //if the assigned status is assign word then return a word to the user
        message.setStatus(Status.ROOM_UPDATE);
        simpMessagingTemplate.convertAndSend("/room", message);
        return message;
    }


    @MessageMapping("/lobbyupdate")
    @SendTo("/room")
    public Message receiveLobbyMessage(@Payload Message message) {
        //if the assigned status is assign word then return a word to the user
        message.setStatus(Status.LOBBY_UPDATE);
        simpMessagingTemplate.convertAndSend("/room", message);
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        return message;
    }

    @MessageMapping( "/gamestart/{roomId}")
    public void startGame(@DestinationVariable Long roomId) {
        if (roomService.checkIfAllReady(roomService.findRoomById(roomId))) {
            chatService.initiateGame(roomService.findRoomById(roomId),roomId);
            chatService.broadcastGameStart(roomId);
            while(!(roomService.findRoomById(roomId).getGameStage().toString().equals(GameStage.END.toString()))) {
                    chatService.conductTurn(roomService.findRoomById(roomId),roomId);
            }
                chatService.broadcastGameEnd(roomService.findRoomById(roomId),roomId);
        }
        else {
            chatService.systemReminder("Not enough players or not all players are ready yet!",roomId);
        }
    }


}
