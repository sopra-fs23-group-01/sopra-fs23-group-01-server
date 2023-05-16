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

 import java.util.*;

 //import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;

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
         user1.setName("Alice");
         user1.setUsername("alice123");
         user1.setPassword("password");
         when(userRepository.getOne(Mockito.anyLong())).thenReturn(user1);

         user2 = new User();
         user2.setId(2L);
         user2.setName("Bob");
         user2.setUsername("bob456");
         user2.setPassword("password");
         when(userRepository.getOne(Mockito.anyLong())).thenReturn(user2);

         user3 = new User();
         user3.setId(3L);
         user3.setName("Alice3");
         user3.setUsername("alice1233");
         user3.setPassword("password1");
         when(userRepository.getOne(Mockito.anyLong())).thenReturn(user3);

         user4 = new User();
         user4.setId(4L);
         user4.setName("Bob4");
         user4.setUsername("bob4567");
         user4.setPassword("password2");
         when(userRepository.getOne(Mockito.anyLong())).thenReturn(user4);

         user5 = new User();
         user5.setId(5L);
         user5.setName("Bob45");
         user5.setUsername("bob45667");
         user5.setPassword("password2");
         when(userRepository.getOne(Mockito.anyLong())).thenReturn(user5);

         user6 = new User();
         user6.setId(6L);
         user6.setName("Bob46");
         user6.setUsername("bob45657");
         user6.setPassword("password2");
         when(userRepository.getOne(Mockito.anyLong())).thenReturn(user6);
         when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
         when(userRepository.getOne(1L)).thenReturn(user1);
         when(userRepository.getOne(2L)).thenReturn(user2);
         when(userRepository.getOne(3L)).thenReturn(user3);
         when(userRepository.getOne(4L)).thenReturn(user4);
         when(userRepository.getOne(5L)).thenReturn(user5);
         when(userRepository.getOne(6L)).thenReturn(user6);
         Room room = new Room();
         room.setTheme(Theme.SPORTS);
         room.setRoomId(1L);
         when(roomRepository.getOne(Mockito.anyLong())).thenReturn(room);
     }

     @Test
     public void testAssignCardsAndRolesInFourPlayersRoom() {
         // Create a new room
         Room room = new Room();
         room.setRoomPlayersList(Arrays.asList(1L, 2L, 3L, 4L));
         room.setTheme(Theme.SPORTS); // Set the theme explicitly
         int i = 0,j = 0;

         // Call the method to assign cards and roles
         roomService.assignCardsAndRoles(room);

         // Check if each player has been assigned a role and card
         for (Long playerId : room.getRoomPlayersList()) {
             User player = userRepository.getOne(playerId);
             //System.out.println("playerId"+player.getId()+"card"+player.getCard()+"Role"+player.getRole());
             if(player.getRole()==Role.UNDERCOVER)
                 i++;
             if (player.getCard().equals("Soccer"))
                 j++;
         }
         assertEquals(i,1);
         assertEquals(j,1);

         i = 0;j = 0;
         room.setTheme(Theme.FURNITURE);
         // Call the method to assign cards and roles
         roomService.assignCardsAndRoles(room);
         // Check if each player has been assigned a role and card
         for (Long playerId : room.getRoomPlayersList()) {
             User player = userRepository.getOne(playerId);
             //System.out.println("playerId"+player.getId()+"card"+player.getCard()+"Role"+player.getRole());
             if(player.getRole()==Role.UNDERCOVER)
                 i++;
             if (player.getCard().equals("Sofa"))
                 j++;
         }
         assertEquals(i,1);
         assertEquals(j,1);
     }

     @Test
     public void testAssignCardsAndRolesInSixPlayersRoom() {
         // Create a new room
         Room room = new Room();
         room.setRoomPlayersList(Arrays.asList(1L, 2L, 3L, 4L,5L,6L));
         room.setTheme(Theme.JOB); // Set the theme explicitly
         int i = 0,j = 0;

         // Call the method to assign cards and roles
         roomService.assignCardsAndRoles(room);

         // Check if each player has been assigned a role and card
         for (Long playerId : room.getRoomPlayersList()) {
             User player = userRepository.getOne(playerId);
             //System.out.println("playerId"+player.getId()+"card"+player.getCard()+"Role"+player.getRole());
             if(player.getRole()==Role.UNDERCOVER)
                 i++;
             if (player.getCard().equals("Policeman"))
                 j++;
         }
         assertEquals(i,2);
         assertEquals(j,2);
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
