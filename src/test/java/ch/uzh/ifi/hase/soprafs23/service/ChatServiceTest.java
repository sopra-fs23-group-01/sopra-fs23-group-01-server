package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



class ChatServiceTest {

    private Room room;
    private UserService userService;
    private RoomService roomService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        room = new Room();
        userService = mock(UserService.class);
        roomService = mock(RoomService.class);
        simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        chatService = new ChatService(roomService, userService, simpMessagingTemplate);
    }

    @Test
    void testConductTurn() {
        // 设置测试数据
        room.setRoomOwnerId(1L);
        room.setGameStage(GameStage.DESCRIPTION);
        room.setAlivePlayersList(Arrays.asList(1L, 2L, 3L));
        room.setCurrentPlayerIndex(0);

        User user1 = new User();
        user1.setUsername("player1");
        User user2 = new User();
        user2.setUsername("player2");
        User user3 = new User();
        user3.setUsername("player3");

        // 配置模拟方法的返回值
        when(userService.getUserById(1L)).thenReturn(user1);
        when(userService.getUserById(2L)).thenReturn(user2);
        when(userService.getUserById(3L)).thenReturn(user3);

        // 调用conductTurn方法
        chatService.conductTurn(room);

        // 等待足够的时间以确保定时任务执行
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 验证预期的方法调用
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), eq("Now it's Playerplayer1's turn to describe"));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), eq("Now it's Playerplayer2's turn to describe"));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), eq("Now it's Playerplayer3's turn to describe"));
    }



}