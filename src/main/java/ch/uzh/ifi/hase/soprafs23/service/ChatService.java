package ch.uzh.ifi.hase.soprafs23.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ch.uzh.ifi.hase.soprafs23.constant.GameStage;
import ch.uzh.ifi.hase.soprafs23.constant.Role;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatService {

    private List<String> words;
    @Lazy
    private final RoomService roomService;
    private final UserService userService;
    //@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    private Map<String, String> userWordMap = new ConcurrentHashMap<>();

    public ChatService(@Lazy RoomService roomService,@Lazy UserService userService, SimpMessagingTemplate simpMessagingTemplate) {
        this.roomService = roomService;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        try {
            words = getWordsRelatedTo("sport");
        } catch (IOException e) {
            e.printStackTrace();
            words = Arrays.asList("bike", "banana", "cherry", "orange", "grape");
        }
    }


    public String getRandomWord() {
        return words.get(new Random().nextInt(words.size()));
    }

    private List<String> getWordsRelatedTo(String query) throws IOException {
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
    public void initiateGame(Room room) {

        room.setCurrentPlayerIndex(0);
        room.setGameStage(GameStage.DESCRIPTION);
        roomService.assignCardsAndRoles(room);
        List<Long> newPlayersList = new ArrayList<>(room.getRoomPlayersList());
        room.setAlivePlayersList(newPlayersList);
        for (Long id : room.getRoomPlayersList()) {
            if (userService.getUserById(id).getRole().equals(Role.DETECTIVE)) room.getDetectivesList().add(id);
            else room.getUndercoversList().add(id);
        }
    }

    public void broadcastGameStart() {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage("Game has started!");
        gameStartMessage.setStatus(Status.START); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", gameStartMessage);
    }

    public void broadcastGameEnd(Room room) {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage("Game has ended!");
        systemReminder("The winner group is "+room.getWinner().toString()+"!");

        gameStartMessage.setStatus(Status.START); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", gameStartMessage);
        roomService.EndGame(room);
    }

    public void broadcastVoteStart() {
        Message voteStartMessage = new Message();
        voteStartMessage.setSenderName("system");
        voteStartMessage.setMessage("Now it's time to vote!\n You can click avatar to vote");
        voteStartMessage.setStatus(Status.VOTE); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", voteStartMessage);
    }

    public void systemReminder(String reminderInfo) {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage(reminderInfo);
        gameStartMessage.setStatus(Status.REMINDER); // 设置状态为 GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/public", gameStartMessage);
    }

    public void conductTurn(Room room){
        AtomicInteger currentPlayerIndex = new AtomicInteger(room.getCurrentPlayerIndex());
        AtomicReference<GameStage> currentGameStage = new AtomicReference<>(room.getGameStage());
        AtomicInteger currentAlivePlayersNum = new AtomicInteger(room.getAlivePlayersList().size());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        if ( currentGameStage.equals(GameStage.DESCRIPTION)){
            while (currentPlayerIndex.get() < currentAlivePlayersNum.get()) {
                if (currentGameStage.equals(GameStage.DESCRIPTION)) {
                    User currentUser = userService.getUserById(room.getAlivePlayersList().get(currentPlayerIndex.get()));
                    systemReminder("Now it's Player" + currentUser.getUsername() + "'s turn to describe");
                    executor.schedule(() -> {
                        // 这里是15秒后要执行的代码
                        if (currentPlayerIndex.get() < currentAlivePlayersNum.get() - 1) {
                            currentPlayerIndex.incrementAndGet();
                        }
                        else {
                            currentPlayerIndex.set(0);
                            currentGameStage.set(GameStage.VOTING);
                        }
                    }, 15, TimeUnit.SECONDS);
                }
            }
        }else if (currentGameStage.equals(GameStage.VOTING)) {
            broadcastVoteStart();
            executor.schedule(() -> {
                // 这里是15秒后要执行的代码
                // 展示投票结果
                // room.getVotingResult();
                roomService.checkIfSomeoneOut(room);
                roomService.checkIfGameEnd(room);
            }, 15, TimeUnit.SECONDS);

            //and go to the next stage of game
            //not all voted so do nothing
        }


    }


    public void userJoin(String username) {
        if (!userWordMap.containsKey(username)) {
            // 从词汇表中随机选择一个单词
            String word = getRandomWord();
            userWordMap.put(username, word);
        }
    }

    public String assignUserRole() {
        List<String> roles = Arrays.asList("detective", "spy");
        return roles.get(new Random().nextInt(roles.size()));
    }
}
