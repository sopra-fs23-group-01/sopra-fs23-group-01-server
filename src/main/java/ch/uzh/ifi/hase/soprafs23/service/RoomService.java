package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.*;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
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
        //newRoom.setRoomOwnerId(newRoom.getRoomOwnerId());
        newRoom.setRoomProperty(RoomProperty.WAITING);
        //newRoom.setTheme(newRoom.getTheme());
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
//        for (Long id: room.getRoomPlayersList()) {
//            if (id == user.getId()) {
//                userService.getUserById(id).setReadyStatus(ReadyStatus.FREE);
//                userService.getUserById(id).setGameStatus(GameStatus.ALIVE);
//                room.getRoomPlayersList().remove(id);
//                break;
//            }
//        }

        if (room.getRoomPlayersList().size()<room.getMaxPlayersNum()){
            room.addRoomPlayerList(user.getId());
        }
        else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This room is full!");
    }

    public void collectVote(Room roomToDo, long voterId, long voteeId, Long roomId) {
        Room room = findRoomById(roomToDo.getRoomId());

        // Check if both voter and votee are alive players
        List<Long> alivePlayersList = room.getAlivePlayersList();
        if (!alivePlayersList.contains(voterId) || !alivePlayersList.contains(voteeId)) {
            return;
        }
        Map<Long, Long> votingResult = room.getVotingResult();
        votingResult.put(voterId, voteeId);
        room.setVotingResult(votingResult);
        //chatService.systemReminder(votingResult.toString() + " collectVote", roomId);
    }

    public boolean checkIfAllVoted(Room room) {
        return room.getVotingResult().size() == room.getAlivePlayersList().size();
    }

    public boolean checkIfAllReady(Room room) {
        int numOfReady = 0;

         for (long id:findRoomById(room.getRoomId()).getRoomPlayersList()){
             if (userService.getUserById(id).getReadyStatus().toString().equals(ReadyStatus.READY.toString())){
                 numOfReady++;
             }
             else break;
        }
         if (numOfReady >= 4 && numOfReady == findRoomById(room.getRoomId()).getRoomPlayersList().size()) {
             return true;
         }
         else {return false;}
    }

    public void assignCardsAndRoles(Room room) {
        int num = room.getRoomPlayersList().size();
        // Get words list according to theme
        List<String> wordsList = assignWordsAccordingToTheme(room.getTheme());
        Random random = new Random();
        int randomNumber = random.nextInt(num);
        // Generate a second random number excluding the first random number
        int secondRandomNumber;
        do {
            secondRandomNumber = random.nextInt(num);
        } while (secondRandomNumber == randomNumber);
        // record words in room
        room.setUndercoverWord(wordsList.get(0));
        room.setDetectiveWord(wordsList.get(1));
        // assign role and card to each player
        for (int i = 0; i < num; i++) {
            User player = userRepository.getOne(room.getRoomPlayersList().get(i));
            if (i == randomNumber || (num >= 6 && i == secondRandomNumber)) {
                player.setRole(Role.UNDERCOVER);
                player.setCard(wordsList.get(0));
            } else {
                player.setRole(Role.DETECTIVE);
                player.setCard(wordsList.get(1));
            }
            userRepository.save(player);
            //chatService.systemReminder(player.getId()+player.getCard(),roomId);
        }
        roomRepository.save(room);

    }

    private List<String> assignWordsAccordingToTheme(Theme theme){
        List<String> wordsList = new ArrayList<>();
        Random random = new Random();
        switch (theme){
            case SPORTS:
                List<String> sportsWords = Arrays.asList("Soccer", "Basketball", "TennisBall", "Baseball", "Bowling");
                Collections.shuffle(sportsWords, random);
                wordsList.addAll(sportsWords.subList(0, 2));
                break;
            case FURNITURE:
                List<String> furnitureWords = Arrays.asList("Sofa", "Chair", "Table", "Bed");
                Collections.shuffle(furnitureWords, random);
                wordsList.addAll(furnitureWords.subList(0, 2));
                break;
            case JOB:
                List<String> jobWords = Arrays.asList("Policeman", "Engineer", "Teacher", "Doctor","Firefighter","Student","Driver");
                int num = random.nextInt(6);
                List<String> randomWords;
                try {
                    randomWords = getWordsRelatedTo(jobWords.get(num));
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Collections.shuffle(randomWords, random);
                wordsList.addAll(randomWords.subList(0, 2));
                break;
        }
        return wordsList;
    }

    public List<String> getWordsRelatedTo(String query) throws IOException {
        List<String> words = new ArrayList<>();
        String apiUrl = "https://api.datamuse.com/words?ml=" + query;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());
                for (JsonNode wordNode : jsonNode) {
                    words.add(wordNode.get("word").asText());
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return words;
    }

    public void checkIfSomeoneOut(Room room, Long roomId) {
        Map<Long, Long> votingResult = room.getVotingResult();
        if (votingResult != null) {
            Map<Long, Integer> voteCounts = new HashMap<>();
            Map<Long, List<Long>> votesByPlayer = new HashMap<>();

            for (Map.Entry<Long, Long> entry : votingResult.entrySet()) {
                Long voterId = entry.getKey();
                Long voteeId = entry.getValue();

                // Increase vote count
                Integer voteCount = voteCounts.get(voteeId);
                if (voteCount == null) {
                    voteCount = 1;
                } else {
                    voteCount += 1;
                }
                voteCounts.put(voteeId, voteCount);

                // Collect voting information by player
                List<Long> votes = votesByPlayer.get(voteeId);
                if (votes == null) {
                    votes = new ArrayList<>();
                }
                votes.add(voterId);
                votesByPlayer.put(voteeId, votes);
            }

            // Broadcast vote information for each player
            for (Map.Entry<Long, List<Long>> entry : votesByPlayer.entrySet()) {
                Long playerId = entry.getKey();
                User player = userService.getUserById(playerId);

                StringBuilder voteInfo = new StringBuilder("Player ")
                        .append(player.getUsername())
                        .append(" is voted by: ");

                List<Long> voters = entry.getValue();
                for (Long voterId : voters) {
                    User voter = userService.getUserById(voterId);
                    voteInfo.append(voter.getUsername()).append(", ");
                }

                // Remove the trailing comma and space
                voteInfo.setLength(voteInfo.length() - 2);

                chatService.systemReminder(voteInfo.toString(), roomId);
            }

            Long mostVotedPlayer = null;
            int maxVotes = -1;

            for (Map.Entry<Long, Integer> entry : voteCounts.entrySet()) {
                Long playerId = entry.getKey();
                int voteCount = entry.getValue();
                if (voteCount > maxVotes) {
                    maxVotes = voteCount;
                    mostVotedPlayer = playerId;
                }else{
                    mostVotedPlayer = null;
                }
            }


            if (mostVotedPlayer != null) {
                User userToBeOuted = userService.getUserById(mostVotedPlayer);
                userToBeOuted.setAliveStatus(false);
                userToBeOuted.setGameStatus(GameStatus.OUT);

                // Prepare voting information string
                StringBuilder voteInfo = new StringBuilder("SO Player ")
                        .append(userToBeOuted.getUsername())
                        .append(" is voted out by: ");

                List<Long> voters = votesByPlayer.get(mostVotedPlayer);
                for (Long voterId : voters) {
                    User voter = userService.getUserById(voterId);
                    voteInfo.append(voter.getUsername()).append(", ");
                }

                // Remove the trailing comma and space
                voteInfo.setLength(voteInfo.length() - 2);

                chatService.systemReminder(voteInfo.toString(), roomId);

                findRoomById(room.getRoomId()).getAlivePlayersList().remove(mostVotedPlayer);

                List<Long> alivePlayerIds = findRoomById(room.getRoomId()).getAlivePlayersList();
                StringBuilder alivePlayersInfo = new StringBuilder("Alive Players: ");
                for (Long playerId : alivePlayerIds) {
                    String username = userService.getUserById(playerId).getUsername();
                    alivePlayersInfo.append(username).append(", ");
                }

                alivePlayersInfo.setLength(alivePlayersInfo.length() - 2);

                chatService.systemReminder(alivePlayersInfo.toString(), roomId);

            } else {
                //systemReminder
                chatService.systemReminder("No players out!", roomId);
            }
        }
    }

    public void checkIfGameEnd(Room roomToDo){
        Room room = findRoomById(roomToDo.getRoomId());
        room.setVotingResult(null);
        int count_un = 0;
        int count_de = 0;
        for (Long id : room.getAlivePlayersList()){
            if (userService.getUserById(id).getRole().equals(Role.DETECTIVE)){count_de++;}
            else {count_un++;}
        }
        if (count_un==0){
            room.setWinner(Role.DETECTIVE);
            room.setGameStage(GameStage.END);
        }
        else if (count_un >= count_de){
            room.setWinner(Role.UNDERCOVER);
            room.setGameStage(GameStage.END);
        }
        else {room.setGameStage(GameStage.DESCRIPTION);}

    }

    public void EndGame(Room roomToDo){
        Room room = findRoomById(roomToDo.getRoomId());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        for (Long id:room.getRoomPlayersList()){
            User user = userService.getUserById(id);
            //chatService.systemReminder("end game", room.getRoomId());
            if(user.getRole().equals(Role.DETECTIVE)){
                user.setNumOfGameDe(user.getNumOfGameDe()+1);
                if(room.getWinner().equals(Role.DETECTIVE)){
                    user.setNumOfWinGameDe(user.getNumOfWinGameDe()+1);
                    user.setRateDe((float)user.getNumOfWinGameDe()/(float)user.getNumOfGameDe());
                    chatService.systemReminder("Player " + user.getUsername() +" winningrate of Detective is now"+decimalFormat.format(user.getRateDe()*100), room.getRoomId());
                }
                else if (room.getWinner().equals(Role.UNDERCOVER)) {
                    user.setRateDe((float)user.getNumOfWinGameDe()/(float)user.getNumOfGameDe());
                    chatService.systemReminder("Player " + user.getUsername() +" winningrate of Detective is now"+decimalFormat.format(user.getRateDe()*100), room.getRoomId());
                }
            }
            else if (user.getRole().equals(Role.UNDERCOVER)) {
                user.setNumOfGameUn(user.getNumOfGameUn()+1);
                if(room.getWinner().equals(Role.DETECTIVE)){
                    user.setRateUn(((float)user.getNumOfWinGameUn())/((float)user.getNumOfGameUn()));
                    chatService.systemReminder("Player " + user.getUsername() +" winningrate of Undercover is now" +decimalFormat.format(user.getRateUn()*100), room.getRoomId());
                }
                else if (room.getWinner().equals(Role.UNDERCOVER)) {
                    user.setNumOfWinGameUn(user.getNumOfWinGameUn()+1);
                    user.setRateUn(((float)user.getNumOfWinGameUn())/((float)user.getNumOfGameUn()));
                    chatService.systemReminder("Player " + user.getUsername() +" winningrate of Undercover is now" +decimalFormat.format(user.getRateUn()*100), room.getRoomId());
                }
            }
            user.setAliveStatus(null);
            user.setGameStatus(GameStatus.ALIVE);
            user.setReadyStatus(ReadyStatus.FREE);
            user.setRole(Role.NOT_ASSIGNED);
            user.setCard(null);
        }
        room.setWinner(null);
        room.setAlivePlayersList(null);
        room.setDetectivesList(null);
        room.setUndercoversList(null);
        room.setGameStage(GameStage.WAITING);
        room.setCurrentPlayerIndex(0);
        room.setRoomProperty(RoomProperty.WAITING);
    }

    public String assignWord(String senderName) {
//        System.out.println(userRepository.findByUsername(senderName).getUsername()+userRepository.findByUsername(senderName).getCard());
        return userRepository.findByUsername(senderName).getCard();
    }
    public String assignSide(String senderName) {
        //System.out.println(userRepository.findByUsername(senderName).getUsername()+userRepository.findByUsername(senderName).getCard());
        return userRepository.findByUsername(senderName).getRole().toString();
    }

    public void deletePlayer(Long userId, Long roomId){
        Room room = findRoomById(roomId);
        if (room.getRoomPlayersList().size()>1){
            room.setRoomOwnerId(room.getRoomPlayersList().get(1));
            room.getRoomPlayersList().remove(userId);
        }
        else{roomRepository.delete(room);}
    }

    public Room findRoomWithMostPlayers(){
        Room roomWithMostPlayers = null;
        int maxPlayers = 0;
        List<Room> roomList = getRooms();

        for (Room room : roomList) {
            if (room.getRoomPlayersList().size() == room.getMaxPlayersNum()){
                continue;
            }

            if (room.getRoomPlayersList().size() > maxPlayers) {
                maxPlayers = room.getRoomPlayersList().size();
                roomWithMostPlayers = room;
            }
        }

        if (roomWithMostPlayers == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No room is available!");
        }
        else{return roomWithMostPlayers;}

    }

    public Room findRoomWithThisPlayer(Long userId){
        Room roomToGo = null;
        for (Room room :getRooms()){
            if (room.getRoomPlayersList().contains(userId)){
                roomToGo = room;
                break;
            }
        }
        return roomToGo;
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
