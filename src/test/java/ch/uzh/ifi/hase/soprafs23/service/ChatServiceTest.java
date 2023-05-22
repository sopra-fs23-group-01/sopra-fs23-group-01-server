package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ChatServiceTest {

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

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
}
