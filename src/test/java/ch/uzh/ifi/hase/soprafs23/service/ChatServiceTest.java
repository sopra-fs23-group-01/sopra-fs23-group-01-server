package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

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
        chatService.conductTurn(room, room.getRoomId());}
        

    @Test
    public void testBroadcastGameStart() {
        // Prepare test data
        Long roomId = 1L;

        // No need to stub anything as we're testing void method and interaction with mocked dependencies

        // Invoke the method under test
        chatService.broadcastGameStart(roomId);

        // Capture the argument and verify that the correct message was sent to the correct room
        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/"+roomId+"/public"), messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();
        
        // Now assert that the properties of the message are as expected
        assertEquals("system", capturedMessage.getSenderName());
        assertEquals("Game has started!", capturedMessage.getMessage());
        assertEquals(Status.START, capturedMessage.getStatus());
    }

    @Test
    public void testSystemReminder() {
        // Prepare test data
        Long roomId = 1L;
        String reminderInfo = "Test Reminder";

        // No need to stub anything as we're testing void method and interaction with mocked dependencies

        // Invoke the method under test
        chatService.systemReminder(reminderInfo, roomId);

        // Capture the argument and verify that the correct message was sent to the correct room
        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/"+roomId+"/public"), messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();
        
        // Now assert that the properties of the message are as expected
        assertEquals("system", capturedMessage.getSenderName());
        assertEquals(reminderInfo, capturedMessage.getMessage());
        assertEquals(Status.REMINDER, capturedMessage.getStatus());
    }

    @Test
    public void testAssignUserRole() {
        Set<String> possibleRoles = new HashSet<>(Arrays.asList("detective", "spy"));
        Set<String> assignedRoles = new HashSet<>();

        // Run the method under test multiple times
        for (int i = 0; i < 100; i++) {
            String role = chatService.assignUserRole();
            // Check that the returned role is either "detective" or "spy"
            assertTrue(possibleRoles.contains(role));
            // Keep track of which roles have been returned
            assignedRoles.add(role);
        }

        // Check that both "detective" and "spy" have been returned at least once
        assertEquals(possibleRoles, assignedRoles);
    }

    @Test
    public void testDescriptionBroadcast() {
        // Prepare test data
        Long roomId = 1L;
        String userName = "Test User";

        // No need to stub anything as we're testing void method and interaction with mocked dependencies

        // Invoke the method under test
        chatService.descriptionBroadcast(userName, roomId);

        // Capture the argument and verify that the correct message was sent to the correct room
        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/"+roomId+"/public"), messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();
        
        // Now assert that the properties of the message are as expected
        assertEquals(userName, capturedMessage.getSenderName());
        assertEquals("Now it's Player --" + userName + "'s turn to describe", capturedMessage.getMessage());
        assertEquals(Status.DESCRIPTION, capturedMessage.getStatus());
    }

    @Test
    void testBroadcastGameEnd() {
        Room room = new Room();
        Long roomId = 1L;

        // set the room object
        room.setWinner(Role.UNDERCOVER);
        room.setUndercoverWord("UndercoverWord");
        room.setDetectiveWord("DetectiveWord");

        chatService.broadcastGameEnd(room, roomId);

        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/" + roomId + "/public"), messageCaptor.capture());

        Message sentMessage = messageCaptor.getValue();
        assertEquals(room.getWinner().toString(), sentMessage.getSenderName());
        assertEquals("Undercover Word:" + room.getUndercoverWord() + "\nDetective Word:" + room.getDetectiveWord(), sentMessage.getMessage());
        assertEquals(Status.END, sentMessage.getStatus());

        verify(roomService).EndGame(room);
    }

    
    @Test
    public void testBroadcastVoteStart() {
        Long roomId = 1L;

        chatService.broadcastVoteStart(roomId);

        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/" + roomId + "/public"), messageCaptor.capture());

        Message sentMessage = messageCaptor.getValue();
        assertEquals("system", sentMessage.getSenderName());
        assertEquals("Now it's time to vote!\n You can click avatar to vote", sentMessage.getMessage());
        assertEquals(Status.VOTE, sentMessage.getStatus());
    }
}
