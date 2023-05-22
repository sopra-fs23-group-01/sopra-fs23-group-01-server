package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class ChatServiceTest {

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private ChatService chatService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        chatService = new ChatService(roomService, userService, simpMessagingTemplate);
    }

    @Test
    public void testInitiateGame() {
        // Prepare test data
        Room room = new Room();
        room.setRoomId(1L);
        List<Long> roomPlayersList = new ArrayList<>();
        roomPlayersList.add(1L);
        roomPlayersList.add(2L);
        room.setRoomPlayersList(roomPlayersList);
        User user = new User();
        user.setId(1L);user.setUsername("1");

        // Stub the dependencies
        when(roomService.findRoomById(room.getRoomId())).thenReturn(room);
        when(userService.getUserById(anyLong())).thenReturn(user);

        // Invoke the method under test
        chatService.initiateGame(room, room.getRoomId());

        // Perform assertions or verifications
        assertEquals(GameStage.DESCRIPTION, room.getGameStage());
        assertEquals(RoomProperty.INGAME, room.getRoomProperty());
        // Verify that the necessary methods were called
        verify(roomService).assignCardsAndRoles(room);
        verify(userService, times(2)).getUserById(anyLong());
    }

    @Test
    public void testConductTurn_4players_DESCRIPTION_phase() throws InterruptedException {
        // Prepare test data
        Room room = new Room();
        room.setRoomId(1L);
        List<Long> alivePlayersList = new ArrayList<>();
        alivePlayersList.add(1L);
        alivePlayersList.add(2L);
        alivePlayersList.add(3L);
        alivePlayersList.add(4L);
        room.setAlivePlayersList(alivePlayersList);
        User user1 = new User();user1.setId(1L);user1.setUsername("1");
        User user2 = new User();user1.setId(2L);user1.setUsername("2");
        User user3 = new User();user1.setId(3L);user1.setUsername("3");
        User user4 = new User();user1.setId(4L);user1.setUsername("4");
        // Stub the dependencies
        when(roomService.findRoomById(room.getRoomId())).thenReturn(room);
        when(userService.getUserById(1L)).thenReturn(user1);
        when(userService.getUserById(2L)).thenReturn(user2);
        when(userService.getUserById(3L)).thenReturn(user3);
        when(userService.getUserById(4L)).thenReturn(user4);
        room.setGameStage(GameStage.DESCRIPTION);
        // Invoke the method under test
        chatService.conductTurn(room, room.getRoomId());
        verify(userService, timeout(6000).times(1)).getUserById(
                anyLong());
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                any(),
                any(Message.class));

    }

    @Test
    public void testConductTurn_1player_DESCRIPTION_phase() throws InterruptedException {
        // Prepare test data
        Room room = new Room();
        room.setRoomId(1L);
        List<Long> alivePlayersList = new ArrayList<>();
        alivePlayersList.add(1L);
        room.setAlivePlayersList(alivePlayersList);
        User user1 = new User();user1.setId(1L);user1.setUsername("1");
        // Stub the dependencies
        when(roomService.findRoomById(room.getRoomId())).thenReturn(room);
        when(userService.getUserById(1L)).thenReturn(user1);
        room.setGameStage(GameStage.DESCRIPTION);
        // Invoke the method under test
        chatService.conductTurn(room, room.getRoomId());
        verify(userService, timeout(6000).times(1)).getUserById(
                anyLong());
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                any(),
                any(Message.class));

    }

    @Test
    public void testConductTurn_VOTING_phase() {
        // Prepare test data
        Room room = new Room();
        room.setRoomId(1L);
        List<Long> alivePlayersList = new ArrayList<>();
        alivePlayersList.add(1L);
        alivePlayersList.add(2L);
        room.setAlivePlayersList(alivePlayersList);
        User user = new User();user.setId(1L);
        user.setUsername("1");
        // Stub the dependencies
        when(roomService.findRoomById(room.getRoomId())).thenReturn(room);
        when(userService.getUserById(anyLong())).thenReturn(user);
        room.setGameStage(GameStage.VOTING);
        // Invoke the method under test
        chatService.conductTurn(room, room.getRoomId());
    }
}
