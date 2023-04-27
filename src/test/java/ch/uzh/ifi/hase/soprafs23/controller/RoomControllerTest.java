package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
    @AutoConfigureMockMvc
    class RoomControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private RoomService roomService;

        @Test
        void getAllRooms() throws Exception {
            // create some test rooms
            Room room1 = new Room();
            room1.setRoomId(10001l);
            room1.setRoomOwnerId(1l);
            room1.setRoomProperty(RoomProperty.PUBLIC);
            roomService.createRoom(room1);

            Room room2 = new Room();
            room2.setRoomId(10002l);
            room2.setRoomOwnerId(2l);
            room2.setRoomProperty(RoomProperty.PUBLIC);
            roomService.createRoom(room2);

            // send GET request to "/games"
            mockMvc.perform(get("/games"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$.[0].roomId").value( 10001l))
                    .andExpect(jsonPath("$.[1].roomId").value(10002l))
                    .andExpect(jsonPath("$.[0].roomOwnerId").value( 1l))
                    .andExpect(jsonPath("$.[1].roomOwnerId").value(2l))
                    .andExpect(jsonPath("$.[0].roomProperty").value( RoomProperty.PUBLIC.toString()))
                    .andExpect(jsonPath("$.[1].roomProperty").value(RoomProperty.PUBLIC.toString()));
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
//
//

        }