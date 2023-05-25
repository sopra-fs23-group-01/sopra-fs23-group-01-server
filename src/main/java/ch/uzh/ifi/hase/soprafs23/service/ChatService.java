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
import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.model.Message;
import ch.uzh.ifi.hase.soprafs23.model.Status;
import org.hibernate.Hibernate;
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

    @Lazy
    private final RoomService roomService;
    private final UserService userService;
    //@Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    private Map<String, String> userWordMap = new ConcurrentHashMap<>();

    public ChatService(@Lazy RoomService roomService,UserService userService, SimpMessagingTemplate simpMessagingTemplate) {
        this.roomService = roomService;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void initiateGame(Room roomToInitiate,Long roomId) {
        Room room = roomService.findRoomById(roomToInitiate.getRoomId());
        room.setCurrentPlayerIndex(0);
        room.setGameStage(GameStage.DESCRIPTION);
        room.setRoomProperty(RoomProperty.INGAME);

        List<Long> newPlayersList = new ArrayList<>(room.getRoomPlayersList());
        room.setAlivePlayersList(newPlayersList);
        roomService.assignCardsAndRoles(room);
        for (Long id : room.getRoomPlayersList()) {
            if (userService.getUserById(id).getRole().equals(Role.DETECTIVE)) {room.getDetectivesList().add(id);
            }
            else {
                room.getUndercoversList().add(id);
            }

        }
    }

    public void broadcastGameStart(Long roomID) {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage("Game has started!");
        gameStartMessage.setStatus(Status.START); //  GAME_STARTED
        simpMessagingTemplate.convertAndSend("/chatroom/"+roomID+"/public", gameStartMessage);
    }

    public void broadcastGameEnd(Room room,Long roomId) {
        Message gameEndMessage = new Message();
        gameEndMessage.setSenderName(room.getWinner().toString());
        gameEndMessage.setMessage("Undercover Word:"+ room.getUndercoverWord()
                +"\nDetective Word:"+ room.getDetectiveWord());
        gameEndMessage.setStatus(Status.END); //  GAME_END
        simpMessagingTemplate.convertAndSend("/chatroom/"+roomId+"/public", gameEndMessage);
        roomService.EndGame(room);
        }

    public void broadcastVoteStart(Long roomId) {
        Message voteStartMessage = new Message();
        voteStartMessage.setSenderName("system");
        voteStartMessage.setMessage("Now it's time to vote!\n You can click avatar to vote");
        voteStartMessage.setStatus(Status.VOTE); // GAME_VOTE
        simpMessagingTemplate.convertAndSend("/chatroom/"+roomId+"/public", voteStartMessage);
    }

    public void systemReminder(String reminderInfo,Long roomId) {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName("system");
        gameStartMessage.setMessage(reminderInfo);
        gameStartMessage.setStatus(Status.REMINDER); // GAME_REMINDER
        simpMessagingTemplate.convertAndSend("/chatroom/"+roomId+"/public", gameStartMessage);
    }

    public void descriptionBroadcast(String userName, Long roomId) {
        Message gameStartMessage = new Message();
        gameStartMessage.setSenderName(userName);
        gameStartMessage.setMessage("Now it's Player --" + userName + "'s turn to describe");
        gameStartMessage.setStatus(Status.DESCRIPTION);
        simpMessagingTemplate.convertAndSend("/chatroom/"+roomId+"/public", gameStartMessage);
    }

    public void conductTurn(Room roomToConduct, Long roomId){
        AtomicInteger i = new AtomicInteger();
        Room room = roomService.findRoomById(roomToConduct.getRoomId());
        AtomicInteger currentPlayerIndex = new AtomicInteger(room.getCurrentPlayerIndex());
        AtomicReference<GameStage> currentGameStage = new AtomicReference<>(room.getGameStage());
        AtomicInteger currentAlivePlayersNum = new AtomicInteger(room.getAlivePlayersList().size());
        if ( currentGameStage.toString().equals(GameStage.DESCRIPTION.toString())){
            while (currentPlayerIndex.get() < currentAlivePlayersNum.get()) {
                    User currentUser = userService.getUserById(room.getAlivePlayersList().get(currentPlayerIndex.get()));
                    descriptionBroadcast(currentUser.getUsername(),roomId);
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    executor.schedule(() -> {
                        if (Integer.parseInt(currentPlayerIndex.toString()) < Integer.parseInt(currentAlivePlayersNum.toString()) - 1) {
                            currentPlayerIndex.incrementAndGet();
                            room.setCurrentPlayerIndex(Integer.parseInt(currentPlayerIndex.toString()));
                        }
                        else {currentPlayerIndex.set(0);
                            room.setCurrentPlayerIndex(0);
                            room.setGameStage(GameStage.VOTING);
                            i.set(1);
                        }
                    }, 15, TimeUnit.SECONDS);//15

                try {
                    Thread.sleep(20000);//20000
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                break;
            }
        }else if (currentGameStage.toString().equals(GameStage.VOTING.toString())) {
            broadcastVoteStart(roomId);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {

                roomService.checkIfSomeoneOut(room,roomId);
                roomService.checkIfGameEnd(room);
            }, 10, TimeUnit.SECONDS);
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }


    }

    private Random random = new Random();
    public String assignUserRole() {
        List<String> roles = Arrays.asList("detective", "spy");
        return roles.get(random.nextInt(roles.size()));
    }
}
