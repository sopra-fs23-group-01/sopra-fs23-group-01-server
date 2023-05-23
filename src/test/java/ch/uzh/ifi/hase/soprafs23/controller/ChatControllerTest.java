package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import ch.uzh.ifi.hase.soprafs23.service.ChatService;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ChatControllerTest {

    @Autowired
    private ChatController chatController;

    @MockBean
    private RoomService roomService;

    @MockBean
    private UserService userService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    private Message message;
    private Long roomId;

    @BeforeEach
    public void setup() {
        roomId = 1L;
        message = new Message();
        message.setSenderName("test user");
        message.setStatus(Status.ASSIGNED_WORD);
    }

//    @Test
//    public void testReceiveMessage() {
//        Mockito.when(roomService.assignWord(any())).thenReturn("test word");
//        Mockito.when(roomService.assignSide(any())).thenReturn("test side");
//
//        chatController.receiveMessage(message, roomId);
//
//        Mockito.verify(roomService).assignWord(eq(message.getSenderName()));
//        Mockito.verify(roomService).assignSide(eq(message.getSenderName()));
//        Mockito.verify(chatService).systemReminder(eq("test word"), eq(roomId));
//        Mockito.verify(simpMessagingTemplate).convertAndSendToUser(eq(message.getSenderName()), eq("/private"), any(Message.class));
//    }

    @Test
    public void testReceiveCreationMessage() {
        chatController.receiveCreationMessage(message);
        
        Mockito.verify(simpMessagingTemplate).convertAndSend(eq("/room"), eq(message));
    }

    @Test
    public void testRecMessage() {
        chatController.recMessage(message);

        Mockito.verify(simpMessagingTemplate).convertAndSendToUser(eq(message.getReceiverName()), eq("/private"), eq(message));
    }


    //测试玩家都准备了开始游戏可以正常开始
    @Test
    public void startGame_AllPlayersReady() {
        Room room = new Room();
        room.setGameStage(GameStage.WAITING);
        when(roomService.findRoomById(roomId)).thenReturn(room);
        when(roomService.checkIfAllReady(room)).thenReturn(true);
    
        // When conductTurn is called, change gameStage to END.
        doAnswer(invocation -> {
            room.setGameStage(GameStage.END);
            return null;
        }).when(chatService).conductTurn(room, roomId);
    
        chatController.startGame(roomId);
        
        //测试是否用到了这些函数
        verify(chatService).initiateGame(room, roomId);
        verify(chatService).broadcastGameStart(roomId);
        verify(chatService).conductTurn(room, roomId);
        verify(chatService).broadcastGameEnd(room, roomId);
    }


    //测试有玩家没有准备但是游戏开始是否正常
    @Test
    public void startGame_NotAllPlayersReady() {
        Room room = new Room();
        room.setGameStage(GameStage.WAITING);

        when(roomService.findRoomById(roomId)).thenReturn(room);
        when(roomService.checkIfAllReady(room)).thenReturn(false);

        chatController.startGame(roomId);

        verify(chatService).systemReminder("Not enough players or not all players are ready yet!",roomId);
        verify(chatService, never()).initiateGame(any(), any());
        verify(chatService, never()).broadcastGameStart(any());
        verify(chatService, never()).conductTurn(any(), any());
        verify(chatService, never()).broadcastGameEnd(any(), any());
    }
}
