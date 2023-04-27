package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.constant.ReadyStatus;
import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class RoomService {

    private final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    @Lazy
    private final ChatService chatService;
    private final UserService userService;

    public RoomService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("roomRepository") RoomRepository roomRepository, ChatService chatService, UserService userService) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.userService = userService;
    }

    public List<Room> getRooms() {return this.roomRepository.findAll();}

    //Here we create a new room and we need to set the room property and theme according to the input from client
    public Room createRoom(Room newRoom) {
        //newRoom.setToken(UUID.randomUUID().toString());
        newRoom.setRoomOwnerId(newRoom.getRoomOwnerId());
        newRoom.setRoomProperty(newRoom.getRoomProperty());
        newRoom.setTheme(newRoom.getTheme());
        newRoom.addRoomPlayerList(newRoom.getRoomOwnerId());
        //newRoom.addRoomPlayer(userRepository.findById(newRoom.getRoomOwnerId()));
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newRoom = roomRepository.save(newRoom);
        roomRepository.flush();
        log.debug("Created Information for Room: {}", newRoom);
        return newRoom;
    }

    public Room findRoomById(Long id) {
        Optional<Room> roomById = roomRepository.findById(id);
        if (roomById.isPresent()) {
            return roomById.get();
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room with this ID:"+id+" not found!");
        }
    }

    public void enterRoom(Room room, User user){
        room.addRoomPlayerList(user.getId());
    }

    public void collectVote(Room roomToDo, long voterId, long voteeId) {
        Room room = findRoomById(roomToDo.getRoomId());
        Map<Long, Long> votingResult = room.getVotingResult();
        votingResult.put(voterId, voteeId);
        room.setVotingResult(votingResult);
        chatService.systemReminder(votingResult.toString()+"collectVote");
    }
    public boolean checkIfAllVoted(Room room) {
        return room.getVotingResult().size() == room.getAlivePlayersList().size();
    }

    public boolean checkIfAllReady(Room room) {
        int numOfReady = 0;
         for (long id:room.getRoomPlayersList()){
             if (userRepository.findById(id).get().getReadyStatus()== ReadyStatus.READY){
                 numOfReady++;
             }
        }
         if (numOfReady == room.getRoomPlayersList().size()) {
             return true;}
         else {return false;}
    }

    public void startGame(Room room){
        room.setCurrentPlayerIndex(0);
        room.setGameStage(GameStage.DESCRIPTION);
        assignCardsAndRoles(room);
    }

    public void assignCardsAndRoles(Room room) {
        int num = room.getRoomPlayersList().size();
        Random random = new Random();
        int randomNumber = random.nextInt(num); // 生成的随机数
        // assign role and card to each player
        for (int i = 0; i < num; i++) {
            User player = userRepository.getOne(room.getRoomPlayersList().get(i));
            if (i == randomNumber) {
                player.setRole(Role.UNDERCOVER);
                player.setCard("pear");

            } else {
                player.setRole(Role.DETECTIVE);
                player.setCard("apple");
            }
            userRepository.save(player);
            chatService.systemReminder(player.getId()+player.getCard());
        }

    }


    public void checkIfSomeoneOut(Room room){
        Map<Long, Long> votingResult = room.getVotingResult();
        chatService.systemReminder("checkIfSomeoneOut"+votingResult.toString());
        if (votingResult != null) {
            Map<Long, Integer> voteCounts = new HashMap<>();
            for (Map.Entry<Long, Long> entry : votingResult.entrySet()) {
                Long voterId = entry.getKey();
                Long voteeId = entry.getValue();

                Integer voteCount = voteCounts.get(voteeId);
                if (voteCount == null) {
                    chatService.systemReminder("140行");
                    voteCount = 1;
                }
                else {
                    voteCount += 1;
                }
                voteCounts.put(voteeId, voteCount);
            }
            chatService.systemReminder(voteCounts.toString()+"148行");

            Long mostVotedPlayer = null;
            int maxVotes = -1;

            for (Map.Entry<Long, Integer> entry : voteCounts.entrySet()) {
                Long playerId = entry.getKey();
                int voteCount = entry.getValue();
                if (voteCount > maxVotes) {
                    maxVotes = voteCount;
                    mostVotedPlayer = playerId;
                }
            }

            if (mostVotedPlayer != null) {
                chatService.systemReminder("163行");
                User userToBeOuted = userService.getUserById(mostVotedPlayer);
                userToBeOuted.setAliveStatus(false);
                chatService.systemReminder("Player " + userToBeOuted.getUsername() +" is voted out!");
                room.getAlivePlayersList().remove(mostVotedPlayer);
            }else {
                //systemReminder
                chatService.systemReminder("No players out!");
            }
        }
    }
    public void checkIfGameEnd(Room roomToDo){
        Room room = findRoomById(roomToDo.getRoomId());
        int count_un = 0;
        int count_de = 0;
        for (Long id : room.getAlivePlayersList()){
            if (userService.getUserById(id).getRole().equals(Role.DETECTIVE)){count_de++;}
            else {count_un++;}
        }
        chatService.systemReminder("180行这"+room.getVotingResult().toString());
        if (count_un==0){
            room.setWinner(Role.DETECTIVE);
            chatService.systemReminder("182行的"+room.getVotingResult().toString());
            room.setGameStage(GameStage.END);
        }
        else if (count_un >= count_de){
            room.setWinner(Role.UNDERCOVER);
            chatService.systemReminder("188行"+room.getVotingResult().toString());
            room.setGameStage(GameStage.END);
        }
        else {room.setGameStage(GameStage.DESCRIPTION);}

    }

    public void EndGame(Room room){
        for (Long id:room.getRoomPlayersList()){
            User user = userService.getUserById(id);
            user.setAliveStatus(null);
            user.setReadyStatus(ReadyStatus.FREE);
            user.setRole(Role.NOT_ASSIGNED);
            user.setCard(null);
            room.setWinner(null);
            room.setAlivePlayersList(null);
            room.setDetectivesList(null);
            room.setUndercoversList(null);
            room.setGameStage(GameStage.WAITING);
            room.setCurrentPlayerIndex(0);
        }
    }

    public String assignWord(String senderName) {
        System.out.println(userRepository.findByUsername(senderName).getCard());
        return userRepository.findByUsername(senderName).getCard();
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */

}
