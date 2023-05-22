package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.constant.Theme;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RoomService roomService;

    @Test
    public void testGetAllRooms() throws Exception{
        //given
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomOwnerId(123l);

        List<Room> allRoom = Collections.singletonList(room);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(roomService.getRooms()).willReturn(allRoom);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomId").value(room.getRoomId()))
                .andExpect(jsonPath("$[0].roomOwnerId").value(room.getRoomOwnerId()));
    }

    @Test
    public void createRoom_validInput() throws Exception {
        // given
        RoomPostDTO roomPostDTO = new RoomPostDTO();
        roomPostDTO.setTheme(Theme.SPORTS);
        roomPostDTO.setRoomOwnerId(1l);
        roomPostDTO.setMaxPlayersNum(4);


        Room createdRoom = new Room();
        createdRoom.setRoomId(10001l);
        createdRoom.setTheme(Theme.SPORTS);
        createdRoom.setRoomOwnerId(1l);
        createdRoom.setMaxPlayersNum(4);

        given(roomService.createRoom(Mockito.any())).willReturn(createdRoom);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/games/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(roomPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").value(createdRoom.getRoomId()))
                .andExpect(jsonPath("$.theme").value(createdRoom.getTheme().toString()))
                .andExpect(jsonPath("$.roomOwnerId").value(createdRoom.getRoomOwnerId()));
    }


    @Test
    public void enterRoom_success() throws Exception {
        // given
        Long roomId = 10001L;
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("john");

        User userInput = new User();
        userInput.setUsername("john");

        Room room = new Room();
        room.setRoomId(roomId);

        given(roomService.findRoomById(roomId)).willReturn(room);

        // when/then -> do the request
        MockHttpServletRequestBuilder putRequest = put("/room/10001/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

    }

    @Test
    public void enterRoom_fail() throws Exception {
        // given
        Long roomId = 10001L;
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("john");

        User userInput = new User();
        userInput.setUsername("john");

        Room room = new Room();
        room.setRoomId(roomId);

        given(roomService.findRoomById((long) 10001))
           .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        // when/then -> do the request
        MockHttpServletRequestBuilder putRequest = put("/room/10001/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isForbidden());

    }

    @Test
    public void testQuickStart_success() throws Exception {
        // 创建输入数据
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        // 创建模拟对象
        User userInput = new User();
        Room roomToStart = new Room();
        roomToStart.setRoomId(1L);
        roomToStart.setTheme(Theme.SPORTS);

        // 设置模拟对象的行为
        when(roomService.findRoomWithMostPlayers()).thenReturn(roomToStart);
        doNothing().when(roomService).enterRoom(any(Room.class), any(User.class));

        // 执行HTTP POST请求
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/room/quickStart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomId").value(roomToStart.getRoomId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.theme").value(roomToStart.getTheme().toString()));
    }

    @Test
    public void testQuickStart_fail() throws Exception {
        // 创建输入数据
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        // 创建模拟对象
        User userInput = new User();
        Room roomToStart = new Room();
        roomToStart.setRoomId(1L);
        roomToStart.setTheme(Theme.SPORTS);

        // 设置模拟对象的行为
        given(roomService.findRoomWithMostPlayers())
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        doNothing().when(roomService).enterRoom(any(Room.class), any(User.class));

        // 执行HTTP POST请求
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/room/quickStart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testPlayerGuard_success() throws Exception {
        // 创建输入数据
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setId(1L);

        // 创建模拟对象
        User userInput = new User();
        userInput.setId(1L);
        Room roomToEnter = new Room();
        roomToEnter.setRoomId(1L);
        roomToEnter.setTheme(Theme.SPORTS);

        // 设置模拟对象的行为
        when(roomService.findRoomWithThisPlayer(userInput.getId())).thenReturn(roomToEnter);

        // 执行HTTP POST请求
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/games/guard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomId").value(roomToEnter.getRoomId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.theme").value(roomToEnter.getTheme().toString()));

    }

    @Test
    public void testPlayerGuard_fail() throws Exception {
        // 创建输入数据
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setId(1L);

        // 创建模拟对象
        User userInput = new User();
        userInput.setId(1L);
        Room roomToEnter = new Room();
        roomToEnter.setRoomId(1L);
        roomToEnter.setTheme(Theme.SPORTS);

        // 设置模拟对象的行为
        given(roomService.findRoomWithThisPlayer(userInput.getId()))
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        // 执行HTTP POST请求
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/games/guard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }


//        @Test
//        void createRoom() throws Exception {
//            // create a test room DTO
//            RoomPostDTO roomPostDTO = new RoomPostDTO();
//            roomPostDTO.setRoomName("Test Room");
//
//            // send POST request to "/games/room" with the test room DTO as the request body
//            mockMvc.perform(post("/games/room")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(asJsonString(roomPostDTO)))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.roomName").value("Test Room"));
//
//            // verify that the room was actually created in the database
//            List<Room> rooms = roomService.getRooms();
//            assertThat(rooms.size()).isEqualTo(1);
//            assertThat(rooms.get(0).getRoomName()).isEqualTo("Test Room");
//        }
//
//        @Test
//        void roomInfo() throws Exception {
//            // create a test room
//            Room room = new Room();
//            room.setRoomName("Test Room");
//            roomService.createRoom(room);
//
//            // send GET request to "/games/{roomId}"
//            mockMvc.perform(get("/games/{roomId}", room.getId()))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.roomName").value("Test Room"));
//        }
//
//        @Test
//        void enterRoom() throws Exception {
//            // create a test room
//            Room room = new Room();
//            room.setRoomName("Test Room");
//            roomService.createRoom(room);
//
//            // create a test user DTO
//            UserPutDTO userPutDTO = new UserPutDTO();
//            userPutDTO.setUsername("Test User");
//
//            // send PUT request to "/room/{roomId}/players" with the test user DTO as the request body
//            mockMvc.perform(put("/room/{roomId}/players", room.getId())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(asJsonString(userPutDTO)))
//                    .andExpect(status().isOk());
//
//            // verify that the user was actually added to the room in the database
//            List<User> players = roomService.getPlayers(room.getId());
//            assertThat(players.size()).isEqualTo(1);
//            assertThat(players.get(0).getUsername()).isEqualTo("Test User");
//        }



    @Test
    void castVote() {
    }

    @Test
    void quickStart() {
    }

    @Test
    void playerGuard() {
    }
//
//

        }