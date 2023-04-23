package ch.uzh.ifi.hase.soprafs23.controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatService chatService;

    private Map<String, String> userWordMap = new ConcurrentHashMap<>();

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message) {
        // 如果是加入房间的消息，那么发送给他们分配的单词
        if (message.getStatus() == Status.JOIN) {
            userJoin(message.getSenderName());
            String assignedWord = userWordMap.get(message.getSenderName());
            Message wordMessage = new Message();
            wordMessage.setSenderName("system");
            wordMessage.setMessage(assignedWord);
            wordMessage.setStatus(Status.ASSIGNED_WORD); // 修改状态为 ASSIGNED_WORD
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

    public void userJoin(String username) {
        if (!userWordMap.containsKey(username)) {
            // 从词汇表中随机选择一个单词
            String word = chatService.getRandomWord();
            userWordMap.put(username, word);
        }
    }
}
