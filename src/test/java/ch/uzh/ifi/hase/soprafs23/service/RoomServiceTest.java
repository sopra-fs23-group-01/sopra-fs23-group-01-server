 package ch.uzh.ifi.hase.soprafs23.service;

 import ch.uzh.ifi.hase.soprafs23.entity.Room;
 import ch.uzh.ifi.hase.soprafs23.constant.*;
 import ch.uzh.ifi.hase.soprafs23.entity.User;
 import ch.uzh.ifi.hase.soprafs23.model.Status;
 import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
 import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Mockito;

 import org.springframework.context.annotation.Lazy;
 import org.springframework.http.HttpStatus;
 import org.springframework.web.server.ResponseStatusException;

 import java.util.*;

 //import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;


 public class RoomServiceTest {

     @Mock
     private UserRepository userRepository;
     @Mock
     private RoomRepository roomRepository;
     @Mock
     private ChatService chatService;
     @Mock
     private UserService userService;
     @InjectMocks
     private RoomService roomService;
     private User user1;
     private User user2;
     private User user3;
     private User user4;
     private User user5;
     private User user6;

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
         user1.setUsername("testUser1");
         user1.setPassword("password");

         user2 = new User();
         user2.setId(2L);
         user2.setUsername("testUser2");
         user2.setPassword("password");

         user3 = new User();
         user3.setId(3L);
         user3.setUsername("testUser3");
         user3.setPassword("password");

         user4 = new User();
         user4.setId(4L);
         user4.setUsername("testUser4");
         user4.setPassword("password");

         user5 = new User();
         user5.setId(5L);
         user5.setUsername("testUser5");
         user5.setPassword("password");

         user6 = new User();
         user6.setId(6L);
         user6.setUsername("testUser6");
         user6.setPassword("password");

         when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
         when(userRepository.getOne(1L)).thenReturn(user1);
         when(userRepository.getOne(2L)).thenReturn(user2);
         when(userRepository.getOne(3L)).thenReturn(user3);
         when(userRepository.getOne(4L)).thenReturn(user4);
         when(userRepository.getOne(5L)).thenReturn(user5);
         when(userRepository.getOne(6L)).thenReturn(user6);/*
         Room room = new Room();
         room.setTheme(Theme.SPORTS);
         room.setRoomId(1L);
         when(roomRepository.getOne(Mockito.anyLong())).thenReturn(room);*/
     }

     @Test
     public void testAssignCardsAndRolesInFourPlayersRoom() {
         // Create a new room
         Room room = new Room();
         room.setRoomPlayersList(Arrays.asList(1L, 2L, 3L, 4L));
         room.setTheme(Theme.SPORTS); // Set the theme explicitly
         int i = 0;

         // Call the method to assign cards and roles
         roomService.assignCardsAndRoles(room);

         // Check if each player has been assigned a role and card
         for (Long playerId : room.getRoomPlayersList()) {
             User player = userRepository.getOne(playerId);
             //System.out.println("playerId"+player.getId()+"card"+player.getCard()+"Role"+player.getRole());
             if(player.getRole()==Role.UNDERCOVER)
                 i++;
         }
         assertEquals(i,1);

         i = 0;
         room.setTheme(Theme.FURNITURE);
         // Call the method to assign cards and roles
         roomService.assignCardsAndRoles(room);
         // Check if each player has been assigned a role and card
         for (Long playerId : room.getRoomPlayersList()) {
             User player = userRepository.getOne(playerId);
             //System.out.println("playerId"+player.getId()+"card"+player.getCard()+"Role"+player.getRole());
             if(player.getRole()==Role.UNDERCOVER)
                 i++;
         }
         assertEquals(i,1);
     }

     @Test
     public void testAssignCardsAndRolesInSixPlayersRoom() {
         // Create a new room
         Room room = new Room();
         room.setRoomPlayersList(Arrays.asList(1L, 2L, 3L, 4L,5L,6L));
         room.setTheme(Theme.JOB); // Set the theme explicitly
         int i = 0;

         // Call the method to assign cards and roles
         roomService.assignCardsAndRoles(room);

         // Check if each player has been assigned a role and card
         for (Long playerId : room.getRoomPlayersList()) {
             User player = userRepository.getOne(playerId);
             System.out.println("playerId"+player.getId()+"card:"+player.getCard());
             if(player.getRole()==Role.UNDERCOVER)
                 i++;
         }
         assertEquals(i,2);
     }

     //test the room creation, since there is no forbidden input for creating room, so there is only success case
     @Test
     void testCreateRoom_success() {
         Room newRoom = new Room();
         newRoom.setRoomOwnerId(1l);
         newRoom.setTheme(Theme.SPORTS);

         // 模拟 roomRepository.save() 方法的行为
         when(roomRepository.save(any(Room.class))).thenReturn(newRoom);

         // 调用 createRoom() 方法
         Room createdRoom = roomService.createRoom(newRoom);

         // 验证 roomRepository.save() 方法被调用一次
         verify(roomRepository, times(1)).save(any(Room.class));

         // 验证返回的房间对象与预期对象相同
         assertEquals(newRoom, createdRoom);
     }


     //when the room is not full, you can enter the room successfully
     @Test
     public void testEnterRoom_success() {
         // 创建一个测试用的房间对象
         Room room = new Room();
         room.setMaxPlayersNum(2);
         room.addRoomPlayerList(123L);

         // 创建一个测试用的用户对象
         User user = new User();
         user.setId(456L);

         // 模拟 userService.getUserById() 方法的行为
         when(userService.getUserById(456L)).thenReturn(user);

         // 调用 enterRoom() 方法
         roomService.enterRoom(room, user);

         // 验证房间的玩家列表中是否包含新加入的用户
         assertTrue(room.getRoomPlayersList().contains(456L));
     }


     //when the room is full, if you want to enter the room, it will throw the exception
     @Test
     public void testEnterRoom_RoomFull() {
         // 创建一个测试用的房间对象
         Room room = new Room();
         room.setMaxPlayersNum(2);
         room.addRoomPlayerList(1l);
         room.addRoomPlayerList(2l);

         // 创建一个测试用的用户对象
         User user = new User();
         user.setId(3l);

         // 调用 enterRoom() 方法，并期望抛出 ResponseStatusException 异常
         assertThrows(ResponseStatusException.class, () -> roomService.enterRoom(room, user));
     }

     //When there are more than one players, when the roomOwner leaves, the roomOwner will be transferred, and this player will be deleted
     @Test
     public void testDeletePlayerWithMultiplePlayers() {
         // 创建一个测试用的房间对象
         Room room = new Room();
         room.setRoomId(1L);
         room.setRoomOwnerId(123L);
         room.addRoomPlayerList(123L);
         room.addRoomPlayerList(456L);

         // 定义要删除的玩家和房间ID
         Long userId = 123L;
         Long roomId = 1L;

         // 模拟 findRoomById() 方法的行为
         when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));

         // 调用 deletePlayer() 方法
         roomService.deletePlayer(userId, roomId);

         // 验证 findRoomById() 方法被调用一次
         verify(roomRepository, times(1)).findById(roomId);

         // 验证房间的玩家列表是否已经移除了指定的玩家
         assertFalse(room.getRoomPlayersList().contains(userId));

         // 验证房间的房主是否已经变更为下一个玩家
         assertEquals(456L, room.getRoomOwnerId());

         // 验证房间是否没有被删除
         verify(roomRepository, never()).delete(room);
     }

     //When the last player leaves the room, the room will be deleted
     @Test
     public void testDeletePlayerWithSinglePlayer() {
         // 创建一个测试用的房间对象
         Room room = new Room();
         room.setRoomId(1L);
         room.setRoomOwnerId(123L);
         room.addRoomPlayerList(123L);

         // 定义要删除的玩家和房间ID
         Long userId = 123L;
         Long roomId = 1L;

         // 模拟 findRoomById() 方法的行为
         when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));

         // 调用 deletePlayer() 方法
         roomService.deletePlayer(userId, roomId);

         // 验证 findRoomById() 方法被调用一次
         verify(roomRepository, times(1)).findById(roomId);

         // 验证房间是否被删除
         verify(roomRepository, times(1)).delete(room);
     }

     @Test
     public void testFindRoomWithMostPlayers_success() {
         // 创建测试用的房间列表
         Room room1 = new Room();
         room1.setRoomId(1L);
         room1.setMaxPlayersNum(4);
         room1.addRoomPlayerList(123L);
         room1.addRoomPlayerList(456L);

         Room room2 = new Room();
         room2.setRoomId(2L);
         room2.setMaxPlayersNum(4);
         room2.addRoomPlayerList(789L);
         room2.addRoomPlayerList(1011L);
         room2.addRoomPlayerList(1213L);

         List<Room> roomList = Arrays.asList(room1, room2);

         // 模拟 getRooms() 方法的行为
         when(roomService.getRooms()).thenReturn(roomList);

         // 调用 findRoomWithMostPlayers() 方法
         Room result = roomService.findRoomWithMostPlayers();

         // 验证 getRooms() 方法被调用一次
         //verify(roomService, times(1)).getRooms();

         // 验证返回的房间对象是否正确
         assertEquals(room2, result);
     }

     @Test
     public void testFindRoomWithMostPlayers_removeFullRooms() {
         // 创建测试用的房间列表
         Room room1 = new Room();
         room1.setRoomId(1L);
         room1.setMaxPlayersNum(4);
         room1.addRoomPlayerList(123L);
         room1.addRoomPlayerList(456L);

         Room room2 = new Room();
         room2.setRoomId(2L);
         room2.setMaxPlayersNum(3);
         room2.addRoomPlayerList(789L);
         room2.addRoomPlayerList(1011L);
         room2.addRoomPlayerList(1213L);

         List<Room> roomList = Arrays.asList(room1, room2);

         // 模拟 getRooms() 方法的行为
         when(roomService.getRooms()).thenReturn(roomList);

         // 调用 findRoomWithMostPlayers() 方法
         Room result = roomService.findRoomWithMostPlayers();

         // 验证 getRooms() 方法被调用一次
         //verify(roomService, times(1)).getRooms();

         // 验证返回的房间对象是否正确
         assertEquals(room1, result);
     }

     @Test
     public void testFindRoomWithMostPlayers_NoAvailableRoom() {
         // 创建一个空的房间列表
         List<Room> roomList = new ArrayList<>();

         // 模拟 getRooms() 方法的行为
         when(roomService.getRooms()).thenReturn(roomList);

         // 调用 findRoomWithMostPlayers() 方法，并捕获预期的异常
         ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
             roomService.findRoomWithMostPlayers();
         });

         // 验证异常的状态码和消息是否符合预期
         assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
         assertEquals("No room is available!", exception.getReason());
     }

     @Test
     public void testFindRoomWithThisPlayer_success() {
         // 创建测试用的房间列表
         Room room1 = new Room();
         room1.setRoomId(1L);
         room1.addRoomPlayerList(123L);
         room1.addRoomPlayerList(456L);

         Room room2 = new Room();
         room2.setRoomId(2L);
         room2.addRoomPlayerList(789L);
         room2.addRoomPlayerList(1011L);
         room2.addRoomPlayerList(1213L);

         List<Room> roomList = Arrays.asList(room1, room2);

         // 模拟 getRooms() 方法的行为
         when(roomService.getRooms()).thenReturn(roomList);

         // 调用 findRoomWithThisPlayer() 方法
         Room result = roomService.findRoomWithThisPlayer(123L);

         // 验证返回的房间对象是否正确
         assertEquals(room1, result);
     }

     @Test
     public void testFindRoomWithThisPlayer_returnNull() {
         // 创建测试用的房间列表
         Room room1 = new Room();
         room1.setRoomId(1L);
         room1.addRoomPlayerList(123L);
         room1.addRoomPlayerList(456L);

         Room room2 = new Room();
         room2.setRoomId(2L);
         room2.addRoomPlayerList(789L);
         room2.addRoomPlayerList(1011L);
         room2.addRoomPlayerList(1213L);

         List<Room> roomList = Arrays.asList(room1, room2);

         // 模拟 getRooms() 方法的行为
         when(roomService.getRooms()).thenReturn(roomList);

         // 调用 findRoomWithThisPlayer() 方法
         Room result = roomService.findRoomWithThisPlayer(1000L);

         // 验证返回的房间对象是否正确
         assertNull(result);
     }







     //    @Test
 //    void testCollectVote() {
 //        // Create a new room with some players and assign their roles
 //        Room room = new Room();
 //        List<Long> players = new ArrayList<>();
 //        User player1 = new User();
 //        User player2 = new User();
 //        players.add(user1.getId());
 //        players.add(user2.getId());
 //        room.setAlivePlayersList(players);
 //        List<Long> deList = new ArrayList<>();
 //        deList.add(user1.getId());
 //        List<Long> unList = new ArrayList<>();
 //        unList.add(user2.getId());
 //        room.setDetectivesList(deList);
 //        room.setUndercoversList(unList);
 //        room.setRoomId(1L);
 //        roomRepository.save(room);
 //        Long roomId = room.getRoomId();
 //
 //
 //        // Cast a vote
 //        long voterId = player1.getId();
 //        long voteeId = player2.getId();
 //        when(roomService.findRoomById(1L)).thenReturn(room);
 //        roomService.collectVote(room, voterId, voteeId);
 //        when(roomService.findRoomById(roomId)).thenReturn(room);
 //        // Check that the vote was recorded
 //        Room updatedRoom = roomService.findRoomById(roomId);
 //        Map<Long, Long> votingResult = updatedRoom.getVotingResult();
 //        assertEquals(voteeId, votingResult.get(voterId));
 //
 //        // Check that the system reminder was sent
 //        verify(chatService, times(1)).systemReminder(anyString());
 //    }

 }



/*
 class RoomServiceTest1 {

     @BeforeEach
     void setUp() {
     }

     @Test
     void getRooms() {
     }



     @Test
     void findRoomById() {
     }


     @Test
     void collectVote() {
     }

     @Test
     void checkIfAllVoted() {
     }

     @Test
     void checkIfAllReady() {
     }

     @Test
     void assignCardsAndRoles() {
     }

     @Test
     void getWordsRelatedTo() {
     }

     @Test
     void checkIfSomeoneOut() {
     }

     @Test
     void checkIfGameEnd() {
     }

     @Test
     void endGame() {
     }

     @Test
     void assignWord() {
     }

     @Test
     void assignSide() {
     }

 }*/
