//package ch.uzh.ifi.hase.soprafs23.service;
//
//import ch.uzh.ifi.hase.soprafs23.constant.Theme;
//import ch.uzh.ifi.hase.soprafs23.entity.Room;
//import ch.uzh.ifi.hase.soprafs23.entity.User;
//import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
//import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
//import ch.uzh.ifi.hase.soprafs23.service.RoomService;
//import ch.uzh.ifi.hase.soprafs23.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@WebAppConfiguration
//@SpringBootTest
//public class RoomServiceIntegrationTest {
//
//    @Autowired
//    private RoomService roomService;
//
//    @MockBean
//    private RoomRepository roomRepository;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private UserService userService;
//
//    @Test
//    public void testCreateRoom() {
//        // Create a new room
//        Room newRoom = new Room();
//        newRoom.setTheme(Theme.SPORTS);
//        newRoom.setRoomOwnerId(1L);
//
//        // Mock the room repository save method
//        when(roomRepository.save(any(Room.class))).thenReturn(newRoom);
//
//        // Call the createRoom method
//        Room createdRoom = roomService.createRoom(newRoom);
//
//        // Verify the room repository save method was called
//        verify(roomRepository, times(1)).save(newRoom);
//
//        // Verify the created room
//        assertNotNull(createdRoom);
//        assertEquals(newRoom.getTheme(), createdRoom.getTheme());
//        assertEquals(newRoom.getRoomOwnerId(), createdRoom.getRoomOwnerId());
//    }
//
//    @Test
//    public void testEnterRoom() {
//        // Create a new user
//        User user = new User();
//        user.setId(1L);
//
//        // Create a new room
//        Room room = new Room();
//        room.setTheme(Theme.SPORTS);
//        room.setRoomOwnerId(1L);
//
//        // Mock the room repository findById method
//        //when(roomRepository.findById(room.getRoomId())).thenReturn(java.util.Optional.of(room));
//        when(roomService.findRoomById(room.getRoomId())).thenReturn(room);
//
//        // Call the enterRoom method
//        roomService.enterRoom(room, user);
//
//        // Verify the room repository findById method was called
//        verify(roomRepository, times(1)).findById(room.getRoomId());
//
//        // Verify that the user has been added to the room
//        assertTrue(room.getRoomPlayersList().contains(user.getId()));
//    }
//
//    @Test
//    public void testCollectVote() {
//        // Create a room
//        Room room = new Room();
//        room.setRoomId(1L);
//        room.setRoomPlayersList(new ArrayList<>());
//
//        // Create users
//        User voter = new User();
//        voter.setId(1L);
//        User votee = new User();
//        votee.setId(2L);
//
//        // Mock the room repository findById method
//        when(roomRepository.findById(room.getRoomId())).thenReturn(java.util.Optional.of(room));
//
//        // Call the collectVote method
//        roomService.collectVote(room, voter.getId(), votee.getId(), room.getRoomId());
//
//        // Verify the room repository findById method was called
//        verify(roomRepository, times(1)).findById(room.getRoomId());
//
//        // Verify the voting result in the room
//        assertEquals(votee.getId(), room.getVotingResult().get(voter.getId()));
//    }
//}

package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.constant.Theme;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;


@WebAppConfiguration
@SpringBootTest
public class RoomServiceIntegrationTest {

    @Qualifier("roomRepository")
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void finish(){
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createRoom_success(){
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setId(1l);
        User user1 = userService.createUser(user);

        Room room = new Room();
        room.setRoomId(10001l);
        room.setRoomOwnerId(user1.getId());
        room.setTheme(Theme.SPORTS);

        Room newRoom = roomService.createRoom(room);

        assertEquals(room.getRoomId(), newRoom.getRoomId());
        assertEquals(room.getTheme(), newRoom.getTheme());
        assertEquals(room.getRoomOwnerId(), newRoom.getRoomOwnerId());
        assertEquals(RoomProperty.WAITING, newRoom.getRoomProperty());
        assertEquals(newRoom.getRoomOwnerId(), user1.getId());
    }

    @Test
    public void enterRoom_success(){
        User user1 = new User();
        user1.setUsername("testUsername1");
        user1.setPassword("password");
        user1.setId(1l);
        User user_1 = userService.createUser(user1);

        User user2 = new User();
        user2.setUsername("testUsername2");
        user2.setPassword("password");
        user2.setId(2l);
        User user_2 = userService.createUser(user2);

        Room room = new Room();
        room.setRoomId(10001l);
        room.setRoomOwnerId(user1.getId());
        room.setMaxPlayersNum(4);
        room.setTheme(Theme.SPORTS);

        Room createdRoom = roomService.createRoom(room);
        roomService.enterRoom(createdRoom, user_2);

        assertEquals(2, createdRoom.getRoomPlayersList().size());
        assertEquals(createdRoom.getRoomPlayersList().get(1), user_2.getId());

    }

    @Test
    public void findRoomById_success(){
        Room room = new Room();
        room.setRoomId(10001l);
        room.setRoomOwnerId(1l);
        room.setTheme(Theme.SPORTS);

        Room createdRoom = roomService.createRoom(room);
        Room foundRoom = roomService.findRoomById(createdRoom.getRoomId());

        assertEquals(createdRoom.getRoomId(), foundRoom.getRoomId());
        assertEquals(createdRoom.getRoomOwnerId(), foundRoom.getRoomOwnerId());
        assertEquals(createdRoom.getTheme(), foundRoom.getTheme());
    }

    @Test
    public void findRoomById_fail(){
        assertThrows(ResponseStatusException.class, () -> roomService.findRoomById(20l));
    }

    @Test
    public void deletePlayer_twoPlayers() {
        // Create user 1
        User user1 = new User();
        user1.setUsername("testUsername1");
        user1.setPassword("password");
        user1.setId(1L);

        // Create user 2
        User user2 = new User();
        user2.setUsername("testUsername2");
        user2.setPassword("password");
        user2.setId(2L);

        // Create room
        Room room = new Room();
        room.setRoomId(10001L);
        room.setRoomOwnerId(user1.getId());
        room.setMaxPlayersNum(4);
        Room createdRoom = roomService.createRoom(room);

        //roomService.enterRoom(createdRoom, user2);
        createdRoom.addRoomPlayerList(user2.getId());
        //roomService.deletePlayer(user1.getId(), createdRoom.getRoomId());
        roomService.leaveRoom(createdRoom, user1.getId());

        assertEquals(1, createdRoom.getRoomPlayersList().size());
        assertEquals(user2.getId(), createdRoom.getRoomOwnerId());
    }

    @Test
    public void deletePlayer_onePlayer() {
        // Create user 1
        User user1 = new User();
        user1.setUsername("testUsername1");
        user1.setPassword("password");
        user1.setId(1L);
        User createdUser1 = userService.createUser(user1);


        // Create room
        Room room = new Room();
        room.setRoomId(10001L);
        room.setRoomOwnerId(createdUser1.getId());
        Room createdRoom = roomService.createRoom(room);
        //roomService.deletePlayer(createdUser1.getId(), createdRoom.getRoomId());
        roomService.leaveRoom(createdRoom, user1.getId());

        assertThrows(ResponseStatusException.class, () -> roomService.findRoomById(createdRoom.getRoomId()));
    }


}
