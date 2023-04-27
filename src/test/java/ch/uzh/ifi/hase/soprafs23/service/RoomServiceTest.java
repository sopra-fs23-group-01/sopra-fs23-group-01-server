package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.constant.*;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RoomServiceTest {

    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private ChatService chatService;
    private UserService userService;
    RoomService roomService;
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    public void setUp() {
        // Set up mock repositories
        userRepository = Mockito.mock(UserRepository.class);
        roomRepository = Mockito.mock(RoomRepository.class);

        // Set up room service
        chatService = Mockito.mock(ChatService.class);
        userService = Mockito.mock(UserService.class);
        roomService = new RoomService(userRepository, roomRepository, chatService, userService);

        // Set up test data
        user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setUsername("alice123");
        user1.setPassword("password");
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user1);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setUsername("bob456");
        user2.setPassword("password");
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user2);

        user3 = new User();
        user3.setId(3L);
        user3.setName("Alice3");
        user3.setUsername("alice1233");
        user3.setPassword("password1");
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user3);

        user4 = new User();
        user4.setId(4L);
        user4.setName("Bob4");
        user4.setUsername("bob4567");
        user4.setPassword("password2");
        Mockito.when(userRepository.getOne(Mockito.anyLong())).thenReturn(user4);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
        Mockito.when(userRepository.getOne(1L)).thenReturn(user1);
        Mockito.when(userRepository.getOne(2L)).thenReturn(user2);
        Mockito.when(userRepository.getOne(3L)).thenReturn(user3);
        Mockito.when(userRepository.getOne(4L)).thenReturn(user4);
        Room room = new Room();
        room.setRoomId(1L);

        Mockito.when(roomRepository.getOne(Mockito.anyLong())).thenReturn(room);
    }

    @Test
    public void testAssignCardsAndRoles() {
        // Create a new room
        Room room = new Room();
        room.setRoomPlayersList(Arrays.asList(1L, 2L, 3L, 4L));

//        userRepository.save(user1);
//        userRepository.save(user2);
        int i = 0,j = 0;

        // Call the method to assign cards and roles
        roomService.assignCardsAndRoles(room);

        // Check if each player has been assigned a role and card
        for (Long playerId : room.getRoomPlayersList()) {
            User player = userRepository.getOne(playerId);
            System.out.println("playerId"+player.getId()+"card"+player.getCard()+"Role"+player.getRole());
            if(player.getRole()==Role.UNDERCOVER)
                i++;
            if (player.getCard().equals("pear"))
                j++;
        }
        assertEquals(i,1);
        assertEquals(j,1);
    }
}
