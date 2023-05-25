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
        createdRoom.addRoomPlayerList(user2.getId());
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
        roomService.leaveRoom(createdRoom, user1.getId());

        assertThrows(ResponseStatusException.class, () -> roomService.findRoomById(createdRoom.getRoomId()));
    }


}
