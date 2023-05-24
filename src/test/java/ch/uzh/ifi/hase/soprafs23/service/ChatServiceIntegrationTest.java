//package ch.uzh.ifi.hase.soprafs23.service;
//
//import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
//import ch.uzh.ifi.hase.soprafs23.constant.Role;
//import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
//import ch.uzh.ifi.hase.soprafs23.entity.Room;
//import ch.uzh.ifi.hase.soprafs23.entity.User;
//import ch.uzh.ifi.hase.soprafs23.model.Message;
//import ch.uzh.ifi.hase.soprafs23.model.Status;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@WebAppConfiguration
//@SpringBootTest
//public class ChatServiceIntegrationTest {
//
//    @Autowired
//    private ChatService chatService;
//
//    @Autowired
//    private RoomService roomService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void initiateGame_validRoom_success() {
//        // Mock data
//        Room roomToInitiate = new Room();
//        roomToInitiate.setRoomId(1L);
//        roomToInitiate.setRoomPlayersList(List.of(1L, 2L, 3L));
//
//        // Mock dependencies
//        when(roomService.findRoomById(1L)).thenReturn(roomToInitiate);
//        when(userService.getUserById(1L)).thenReturn(new User());
//        when(userService.getUserById(2L)).thenReturn(new User());
//        when(userService.getUserById(3L)).thenReturn(new User());
//
//        // Perform the test
//        chatService.initiateGame(roomToInitiate, 1L);
//
//        // Verify the interactions
//        verify(roomService, times(1)).findRoomById(1L);
//        verify(roomService, times(1)).assignCardsAndRoles(roomToInitiate);
//    }
//
//    @Test
//    void broadcastGameStart_validRoom_success() {
//        // Mock data
//        Long roomId = 1L;
//
//        // Perform the test
//        chatService.broadcastGameStart(roomId);
//
//        // Verify the interactions
//        verify(simpMessagingTemplate, times(1)).convertAndSend("/chatroom/" + roomId + "/public", any(Message.class));
//    }
//
//    // ... Add more test cases for other methods
//
//
//
//    @Test
//    public void broadcastVoteStart_ValidRoom_ShouldSendMessageToChatroom() {
//        // Create a mock Room object
//        Room room = new Room();
//        room.setRoomId(1L);
//
//        // Call the method to be tested
//        chatService.broadcastVoteStart(1L);
//
//        // Verify that SimpMessagingTemplate's convertAndSend method is called with the correct arguments
//        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/1/public"), any(Message.class));
//    }
//
//    @Test
//    public void systemReminder_ValidReminderInfoAndRoom_ShouldSendMessageToChatroom() {
//        // Create a mock Room object
//        Room room = new Room();
//        room.setRoomId(1L);
//
//        // Call the method to be tested
//        chatService.systemReminder("Reminder message", 1L);
//
//        // Verify that SimpMessagingTemplate's convertAndSend method is called with the correct arguments
//        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/1/public"), any(Message.class));
//    }
//
//    @Test
//    public void descriptionBroadcast_ValidUserNameAndRoom_ShouldSendMessageToChatroom() {
//        // Create a mock Room object
//        Room room = new Room();
//        room.setRoomId(1L);
//
//        // Call the method to be tested
//        chatService.descriptionBroadcast("user1", 1L);
//
//        // Verify that SimpMessagingTemplate's convertAndSend method is called with the correct arguments
//        verify(simpMessagingTemplate).convertAndSend(eq("/chatroom/1/public"), any(Message.class));
//    }
//
//    @Test
//    public void conductTurn_DescriptionGameStage_ShouldSendMessageToChatroomAndScheduleTasks() throws InterruptedException {
//        // Create a mock Room object
//        Room room = new Room();
//        room.setRoomId(1L);
//        room.setGameStage(GameStage.DESCRIPTION);
//        room.setCurrentPlayerIndex(0);
//        room.setAlivePlayersList(List.of(1L, 2L, 3L));
//
//        // Create a mock User object
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setUsername("user1");
//        User user2 = new User();
//        user2.setId(2L);
//        user2.setUsername("user2");
//        User user3 = new User();
//        user3.setId(3L);
//        user3.setUsername("user3");
//
//        // Mock the behavior of UserService
//        when(userService.getUserById(1L)).thenReturn(user1);
//        when(userService.getUserById(2L)).thenReturn(user2);
//        when(userService.getUserById(3L)).thenReturn(user3);
//
//        // Mock the behavior of RoomService
//        when(roomService.findRoomById(1L)).thenReturn(room);
//
//        // Mock the behavior of SimpMessagingTemplate
//        doAnswer(invocation -> {
//            Message message = invocation.getArgument(1);
//            // Verify that the correct message is sent to the chatroom
//            // You can add more assertions based on the expected behavior
//            assertEquals("Now it's Player --user1's turn to describe", message.getMessage());
//            return null;
//        }).when(simpMessagingTemplate).convertAndSend(anyString(), any(Message.class));
//
//        // Create a CountDownLatch to wait for the tasks to complete
//        CountDownLatch latch = new CountDownLatch(1);
//
//        // Create a ScheduledExecutorService to schedule the tasks
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//        // Execute the method to be tested
//        executorService.submit(() -> {
//            chatService.conductTurn(room, 1L);
//            latch.countDown();
//        });
//
//        // Wait for the tasks to complete
//        latch.await();
//
//        // Verify that the room's currentPlayerIndex and gameStage are updated correctly
//        assertEquals(1, room.getCurrentPlayerIndex());
//        assertEquals(GameStage.DESCRIPTION, room.getGameStage());
//    }
//
//    @Test
//    public void conductTurn_AllPlayersDescribed_ShouldEnterVotingStage() throws InterruptedException {
//        // Create a mock Room object
//        Room room = new Room();
//        room.setRoomId(1L);
//        room.setGameStage(GameStage.DESCRIPTION);
//        room.setCurrentPlayerIndex(0);
//        room.setAlivePlayersList(List.of(1L));
//
//        // Create a mock User object
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setUsername("user1");
//
//        // Mock the behavior of UserService
//        when(userService.getUserById(1L)).thenReturn(user1);
//
//        // Mock the behavior of RoomService
//        when(roomService.findRoomById(1L)).thenReturn(room);
//
//        // Mock the behavior of SimpMessagingTemplate
//        doAnswer(invocation -> {
//            Message message = invocation.getArgument(1);
//            // No need to assert the message in this test case
//            return null;
//        }).when(simpMessagingTemplate).convertAndSend(anyString(), any(Message.class));
//
//        // Create a CountDownLatch to wait for the tasks to complete
//        CountDownLatch latch = new CountDownLatch(1);
//
//        // Create a ScheduledExecutorService to schedule the tasks
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//        // Execute the method to be tested
//        executorService.submit(() -> {
//            chatService.conductTurn(room, 1L);
//            latch.countDown();
//        });
//
//        // Wait for the tasks to complete
//        latch.await();
//
//        // Verify that the room's currentPlayerIndex and gameStage are updated correctly
//        assertEquals(0, room.getCurrentPlayerIndex());
//        assertEquals(GameStage.VOTING, room.getGameStage());
//    }
//
//
//}
