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
         when(userRepository.getOne(6L)).thenReturn(user6);
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

         when(roomRepository.save(any(Room.class))).thenReturn(newRoom);

         Room createdRoom = roomService.createRoom(newRoom);

         verify(roomRepository, times(1)).save(any(Room.class));

         assertEquals(newRoom, createdRoom);
     }


     //when the room is not full, you can enter the room successfully
     @Test
     public void testEnterRoom_success() {

         Room room = new Room();
         room.setMaxPlayersNum(2);
         room.addRoomPlayerList(123L);

         User user = new User();
         user.setId(456L);

         when(userService.getUserById(456L)).thenReturn(user);

         roomService.enterRoom(room, user);

         assertTrue(room.getRoomPlayersList().contains(456L));
     }


     //when the room is full, if you want to enter the room, it will throw the exception
     @Test
     public void testEnterRoom_RoomFull() {

         Room room = new Room();
         room.setMaxPlayersNum(2);
         room.addRoomPlayerList(1l);
         room.addRoomPlayerList(2l);

         User user = new User();
         user.setId(3l);

         assertThrows(ResponseStatusException.class, () -> roomService.enterRoom(room, user));
     }

     //When there are more than one players, when the roomOwner leaves, the roomOwner will be transferred, and this player will be deleted
     @Test
     public void testDeletePlayerWithMultiplePlayers() {

         Room room = new Room();
         room.setRoomId(1L);
         room.setRoomOwnerId(123L);
         room.addRoomPlayerList(123L);
         room.addRoomPlayerList(456L);

         Long userId = 123L;
         Long roomId = 1L;

         when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));

         roomService.deletePlayer(userId, roomId);

         verify(roomRepository, times(1)).findById(roomId);

         assertFalse(room.getRoomPlayersList().contains(userId));

         assertEquals(456L, room.getRoomOwnerId());

         verify(roomRepository, never()).delete(room);
     }

     //When the last player leaves the room, the room will be deleted
     @Test
     public void testDeletePlayerWithSinglePlayer() {
         Room room = new Room();
         room.setRoomId(1L);
         room.setRoomOwnerId(123L);
         room.addRoomPlayerList(123L);

         Long userId = 123L;
         Long roomId = 1L;

         when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));

         roomService.deletePlayer(userId, roomId);

         verify(roomRepository, times(1)).findById(roomId);

         verify(roomRepository, times(1)).delete(room);
     }

     @Test
     public void testFindRoomWithMostPlayers_success() {

         Room room1 = new Room();
         room1.setRoomId(1L);
         room1.setRoomProperty(RoomProperty.WAITING);
         room1.setMaxPlayersNum(4);
         room1.addRoomPlayerList(123L);
         room1.addRoomPlayerList(456L);

         Room room2 = new Room();
         room2.setRoomId(2L);
         room2.setRoomProperty(RoomProperty.WAITING);
         room2.setMaxPlayersNum(4);
         room2.addRoomPlayerList(789L);
         room2.addRoomPlayerList(1011L);
         room2.addRoomPlayerList(1213L);

         List<Room> roomList = Arrays.asList(room1, room2);

         when(roomService.getRooms()).thenReturn(roomList);

         Room result = roomService.findRoomWithMostPlayers();

         assertEquals(room2, result);
     }

     @Test
     public void testFindRoomWithMostPlayers_removeFullRooms() {

         Room room1 = new Room();
         room1.setRoomId(1L);
         room1.setMaxPlayersNum(4);
         room1.setRoomProperty(RoomProperty.WAITING);
         room1.addRoomPlayerList(123L);
         room1.addRoomPlayerList(456L);

         Room room2 = new Room();
         room2.setRoomId(2L);
         room2.setMaxPlayersNum(3);
         room2.setRoomProperty(RoomProperty.WAITING);
         room2.addRoomPlayerList(789L);
         room2.addRoomPlayerList(1011L);
         room2.addRoomPlayerList(1213L);

         List<Room> roomList = Arrays.asList(room1, room2);

         when(roomService.getRooms()).thenReturn(roomList);

         Room result = roomService.findRoomWithMostPlayers();

         assertEquals(room1, result);
     }

     @Test
     public void testFindRoomWithMostPlayers_NoAvailableRoom() {

         List<Room> roomList = new ArrayList<>();

         when(roomService.getRooms()).thenReturn(roomList);

         ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
             roomService.findRoomWithMostPlayers();
         });

         assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
         assertEquals("No room is available!", exception.getReason());
     }

     @Test
     public void testFindRoomWithThisPlayer_success() {

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

         when(roomService.getRooms()).thenReturn(roomList);

         Room result = roomService.findRoomWithThisPlayer(123L);

         assertEquals(room1, result);
     }

     @Test
     public void testFindRoomWithThisPlayer_returnNull() {

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

         when(roomService.getRooms()).thenReturn(roomList);

         Room result = roomService.findRoomWithThisPlayer(1000L);

         assertNull(result);
     }

     @Test
     public void testFindRoomById_existingRoomId_shouldReturnRoom() {
         // Arrange
         Long roomId = 1L;
         Room room = new Room();
         when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

         // Act
         Room result = roomService.findRoomById(roomId);

         // Assert
         assertEquals(room, result);
     }

     @Test
     public void testFindRoomById_nonExistingRoomId_shouldThrowException() {
         // Arrange
         Long roomId = 1L;
         when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

         // Act and Assert
         assertThrows(ResponseStatusException.class, () -> {
             roomService.findRoomById(roomId);
         });
     }

     @Test
     public void testCheckIfSomeoneOut_votesExist_shouldVoteOutPlayer() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         Map<Long, Long> votingResult = new HashMap<>();
         votingResult.put(1L, 2L);
         votingResult.put(3L, 2L);
         votingResult.put(2L, 1L);
         room.setVotingResult(votingResult);

         User user1 = new User();
         user1.setId(1L);
         user1.setUsername("testUser1");
         user1.setAliveStatus(true);
         user1.setGameStatus(GameStatus.ALIVE);

         User user2 = new User();
         user2.setId(2L);
         user2.setUsername("testUser2");
         user2.setAliveStatus(true);
         user2.setGameStatus(GameStatus.ALIVE);

         User user3 = new User();
         user3.setId(3L);
         user3.setUsername("testUser3");
         user3.setAliveStatus(true);
         user3.setGameStatus(GameStatus.ALIVE);

         when(userService.getUserById(1L)).thenReturn(user1);
         when(userService.getUserById(2L)).thenReturn(user2);
         when(userService.getUserById(3L)).thenReturn(user3);

         List<Long> playersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L,3L));
         room.setRoomPlayersList(playersList);
         room.setAlivePlayersList(alivePlayersList);

         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
         when(userRepository.save(user1)).thenReturn(user1);
         when(userRepository.save(user2)).thenReturn(user2);
         when(userRepository.save(user3)).thenReturn(user3);

         // Act
         roomService.checkIfSomeoneOut(room, 1L);

         // Assert
         assertFalse(user2.getAliveStatus());
         assertEquals(GameStatus.OUT, user2.getGameStatus());
         assertFalse(room.getAlivePlayersList().contains(2L));
     }


     @Test
     public void testCheckIfSomeoneOut_noVotesExist_shouldNotVoteOutPlayer() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);
         room.setVotingResult(null);

         // Act
         roomService.checkIfSomeoneOut(room, 1L);

         // Assert
         verifyZeroInteractions(userService);
         verifyZeroInteractions(chatService);
     }

     @Test
     public void testCheckIfSomeoneOut_tieVote_shouldNotVoteOutPlayer() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         Map<Long, Long> votingResult = new HashMap<>();
         votingResult.put(1L, 2L);
         votingResult.put(2L, 3L);
         votingResult.put(3L, 1L);
         room.setVotingResult(votingResult);

         User user1 = new User();
         user1.setId(1L);
         user1.setUsername("testUser1");
         user1.setAliveStatus(true);
         user1.setGameStatus(GameStatus.ALIVE);

         User user2 = new User();
         user2.setId(2L);
         user2.setUsername("testUser2");
         user2.setAliveStatus(true);
         user2.setGameStatus(GameStatus.ALIVE);

         User user3 = new User();
         user3.setId(3L);
         user3.setUsername("testUser3");
         user3.setAliveStatus(true);
         user3.setGameStatus(GameStatus.ALIVE);

         when(userService.getUserById(1L)).thenReturn(user1);
         when(userService.getUserById(2L)).thenReturn(user2);
         when(userService.getUserById(3L)).thenReturn(user3);


         List<Long> playersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L,3L));
         room.setRoomPlayersList(playersList);
         room.setAlivePlayersList(alivePlayersList);

         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.checkIfSomeoneOut(room, 1L);

         // Assert
         assertTrue(user1.getAliveStatus());
         assertEquals(GameStatus.ALIVE, user1.getGameStatus());
         assertTrue(user2.getAliveStatus());
         assertEquals(GameStatus.ALIVE, user2.getGameStatus());
         assertTrue(user3.getAliveStatus());
         assertEquals(GameStatus.ALIVE, user3.getGameStatus());
         assertTrue(room.getAlivePlayersList().containsAll(Arrays.asList(1L, 2L, 3L)));
         verify(chatService, times(1)).systemReminder(eq("No players out!"), eq(1L));
     }

     @Test
     public void testCollectVote_validVote_shouldUpdateVotingResultAndNotify() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> playersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L,3L));
         room.setRoomPlayersList(playersList);
         room.setAlivePlayersList(alivePlayersList);

         Map<Long, Long> votingResult = new HashMap<>();
         room.setVotingResult(votingResult);

         long voterId = 1L;
         long voteeId = 2L;
         long roomId = 1L;

         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.collectVote(room, voterId, voteeId, roomId);

         // Assert
         Map<Long, Long> updatedVotingResult = room.getVotingResult();
         assertEquals(voteeId, updatedVotingResult.get(voterId));
     }


     @Test
     public void testCollectVote_outPlayerCannotVote_shouldNotUpdateVotingResult() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> playersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L));
         room.setRoomPlayersList(playersList);
         room.setAlivePlayersList(alivePlayersList);

         Map<Long, Long> votingResult = new HashMap<>();
         room.setVotingResult(votingResult);

         long voterId = 3L; // OUT player
         long voteeId = 1L;
         long roomId = 1L;

         User outPlayer = new User();
         outPlayer.setId(3L);
         outPlayer.setAliveStatus(false);
         outPlayer.setGameStatus(GameStatus.OUT);

         when(userService.getUserById(3L)).thenReturn(outPlayer);
         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.collectVote(room, voterId, voteeId, roomId);

         // Assert
         Map<Long, Long> updatedVotingResult = room.getVotingResult();
         assertTrue(updatedVotingResult.isEmpty());
         verify(chatService, never()).systemReminder(anyString(), anyLong());
     }

     @Test
     public void testCheckIfAllReady_allPlayersReady_shouldReturnTrue() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> roomPlayersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L));
         room.setRoomPlayersList(roomPlayersList);

         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         for (long id : roomPlayersList) {
             User user = new User();
             user.setId(id);
             user.setReadyStatus(ReadyStatus.READY);
             when(userService.getUserById(id)).thenReturn(user);
         }

         // Act
         boolean result = roomService.checkIfAllReady(room);

         // Assert
         assertTrue(result);
     }

     @Test
     public void testCheckIfAllReady_notAllPlayersReady_shouldReturnFalse() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> roomPlayersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L));
         room.setRoomPlayersList(roomPlayersList);

         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         User readyUser = new User();
         readyUser.setId(1L);
         readyUser.setReadyStatus(ReadyStatus.READY);
         when(userService.getUserById(1L)).thenReturn(readyUser);

         User notReadyUser = new User();
         notReadyUser.setId(2L);
         notReadyUser.setReadyStatus(ReadyStatus.FREE);
         when(userService.getUserById(2L)).thenReturn(notReadyUser);

         // Act
         boolean result = roomService.checkIfAllReady(room);

         // Assert
         assertFalse(result);
     }

     @Test
     public void testCheckIfGameEnd_detectiveWins_shouldSetWinnerAndGameStageToEnd() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
         room.setAlivePlayersList(alivePlayersList);

         User detective1 = new User();
         detective1.setId(1L);
         detective1.setRole(Role.DETECTIVE);

         User detective2 = new User();
         detective2.setId(2L);
         detective2.setRole(Role.DETECTIVE);

         User detective3 = new User();
         detective3.setId(3L);
         detective3.setRole(Role.DETECTIVE);

         when(userService.getUserById(1L)).thenReturn(detective1);
         when(userService.getUserById(2L)).thenReturn(detective2);
         when(userService.getUserById(3L)).thenReturn(detective3);
         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.checkIfGameEnd(room);

         // Assert
         assertEquals(Role.DETECTIVE, room.getWinner());
         assertEquals(GameStage.END, room.getGameStage());
     }

     @Test
     public void testCheckIfGameEnd_undercoverWins_shouldSetWinnerAndGameStageToEnd() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L));
         room.setAlivePlayersList(alivePlayersList);

         User detective1 = new User();
         detective1.setId(1L);
         detective1.setRole(Role.DETECTIVE);

         User detective2 = new User();
         detective2.setId(2L);
         detective2.setRole(Role.UNDERCOVER);

         User undercover2 = new User();
         undercover2.setId(3L);
         undercover2.setRole(Role.UNDERCOVER);

         User undercover3 = new User();
         undercover3.setId(4L);
         undercover3.setRole(Role.UNDERCOVER);

         when(userService.getUserById(1L)).thenReturn(detective1);
         when(userService.getUserById(2L)).thenReturn(detective2);
         when(userService.getUserById(3L)).thenReturn(undercover2);
         when(userService.getUserById(4L)).thenReturn(undercover3);
         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.checkIfGameEnd(room);

         // Assert
         assertEquals(Role.UNDERCOVER, room.getWinner());
         assertEquals(GameStage.END, room.getGameStage());
     }

     @Test
     public void testCheckIfGameEnd_gameContinues_shouldSetGameStageToDescription() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);

         List<Long> alivePlayersList = new ArrayList<>(Arrays.asList(1L, 2L,3L));
         room.setAlivePlayersList(alivePlayersList);

         User detective1 = new User();
         detective1.setId(1L);
         detective1.setRole(Role.DETECTIVE);

         User detective2 = new User();
         detective2.setId(2L);
         detective2.setRole(Role.DETECTIVE);

         User undercover = new User();
         undercover.setId(3L);
         undercover.setRole(Role.UNDERCOVER);

         when(userService.getUserById(1L)).thenReturn(detective1);
         when(userService.getUserById(2L)).thenReturn(detective2);
         when(userService.getUserById(3L)).thenReturn(undercover);
         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.checkIfGameEnd(room);

         // Assert
         assertEquals(GameStage.DESCRIPTION, room.getGameStage());
     }

     @Test
     public void testEndGame_updateStatisticsAndResetRoomProperties() {
         // Arrange
         Room room = new Room();
         room.setRoomId(1L);
         room.setWinner(Role.UNDERCOVER);

         List<Long> roomPlayersList = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
         room.setRoomPlayersList(roomPlayersList);

         User detective = new User();
         detective.setId(1L);
         detective.setUsername("Detective");
         detective.setRole(Role.DETECTIVE);
         detective.setNumOfGameDe(1);
         detective.setNumOfWinGameDe(1);
         detective.setRateDe(1f);

         User undercover1 = new User();
         undercover1.setId(2L);
         undercover1.setUsername("Undercover1");
         undercover1.setRole(Role.UNDERCOVER);
         undercover1.setNumOfGameUn(0);
         undercover1.setNumOfWinGameUn(0);
         undercover1.setRateUn(0f);

         User undercover2 = new User();
         undercover2.setId(3L);
         undercover2.setUsername("Undercover2");
         undercover2.setRole(Role.UNDERCOVER);
         undercover2.setNumOfGameUn(1);
         undercover2.setNumOfWinGameUn(0);
         undercover2.setRateUn(0f);

         when(userService.getUserById(1L)).thenReturn(detective);
         when(userService.getUserById(2L)).thenReturn(undercover1);
         when(userService.getUserById(3L)).thenReturn(undercover2);
         when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

         // Act
         roomService.EndGame(room);

         // Assert
         assertEquals(Role.NOT_ASSIGNED, detective.getRole());
         assertEquals(2, detective.getNumOfGameDe());
         assertEquals(1, detective.getNumOfWinGameDe());
         assertEquals(0.5f, detective.getRateDe(), 0.001f);

         assertEquals(Role.NOT_ASSIGNED, undercover1.getRole());
         assertEquals(1, undercover1.getNumOfGameUn());
         assertEquals(1, undercover1.getNumOfWinGameUn());
         assertEquals(1.0f, undercover1.getRateUn(), 0.001f);

         assertEquals(Role.NOT_ASSIGNED, undercover2.getRole());
         assertEquals(2, undercover2.getNumOfGameUn());
         assertEquals(1, undercover2.getNumOfWinGameUn());
         assertEquals(0.5f, undercover2.getRateUn(), 0.001f);

         assertNull(room.getWinner());
         assertNull(room.getAlivePlayersList());
         assertNull(room.getDetectivesList());
         assertNull(room.getUndercoversList());
         assertEquals(GameStage.WAITING, room.getGameStage());
         assertEquals(0, room.getCurrentPlayerIndex());
         assertEquals(RoomProperty.WAITING, room.getRoomProperty());

     }

 }
