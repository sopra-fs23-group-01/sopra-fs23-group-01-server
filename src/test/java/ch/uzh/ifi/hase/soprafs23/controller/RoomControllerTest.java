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

    @MockBean
    private DTOMapper mapper;

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
    @Test
    public void testCastVote_success() throws Exception {
        // Prepare input data
        Long roomId = 1L;
        Long voterId = 2L;
        Long voteeId = 3L;

        // Create mock objects
        Room room = new Room();
        // Set necessary properties of the room object

        // Configure mock behavior
        given(roomService.findRoomById(roomId)).willReturn(room);

        // Execute the request
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/room/{roomId}/vote/{voterId}={voteeId}", roomId, voterId, voteeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Add assertions or verifications if needed
        // For example:
        verify(roomService).collectVote(eq(room), eq(voterId), eq(voteeId), eq(roomId));
    }

    @Test
    public void testCastVote_roomNotFound() throws Exception {
        // Prepare input data
        Long roomId = 1L;
        Long voterId = 2L;
        Long voteeId = 3L;

        // Configure mock behavior to throw exception
        given(roomService.findRoomById(roomId)).willReturn(null);

        // Execute the request
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/room/{roomId}/vote/{voterId}={voteeId}", roomId, voterId, voteeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        // Add assertions or verifications if needed
        // For example:
        verify(roomService, never()).collectVote(any(Room.class), anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testGetRoomInfo() throws Exception {
        // given
        Long roomId = 1L;
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomOwnerId(123L);

        RoomGetDTO expectedDTO = new RoomGetDTO();
        expectedDTO.setRoomId(roomId);
        expectedDTO.setRoomOwnerId(123L);

        given(roomService.findRoomById(roomId)).willReturn(room);
        given(mapper.convertEntityToRoomGetDTO(room)).willReturn(expectedDTO);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(roomId))
                .andExpect(jsonPath("$.roomOwnerId").value(123L));
    }

}