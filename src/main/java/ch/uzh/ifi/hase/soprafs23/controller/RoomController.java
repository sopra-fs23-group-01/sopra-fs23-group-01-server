package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class RoomController {

    private final RoomService roomService;

    RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoomGetDTO> getAllRooms() {
        // fetch all users in the internal representation，
        List<Room> rooms = roomService.getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Room room : rooms) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room));
        }
        return roomGetDTOs;
    }

    @PostMapping("/games/room")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomGetDTO createRoom(@RequestBody RoomPostDTO roomPostDTO) {
        // convert API user to internal representation
        Room roomInput = DTOMapper.INSTANCE.convertRoomPostDTOtoEntity(roomPostDTO);
        //roomInput.getRoomOwnerId();
        // create user
        Room createdRoom = roomService.createRoom(roomInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(createdRoom);
    }

    //Get method for getting one room
    @GetMapping("/games/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoomGetDTO roomInfo (@PathVariable("roomId") Long roomId) {
        Room room = roomService.findRoomById(roomId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);
    }


    @PutMapping("/room/{roomId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void enterRoom(@PathVariable("roomId") Long roomId, @RequestBody UserPutDTO userPutDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        Room room = roomService.findRoomById(roomId);
        roomService.enterRoom(room, userInput);
    }

    @PutMapping("/room/{roomId}/vote/{voterId}={voteeId}")
    @ResponseStatus(HttpStatus.OK)
    public void castVote(@PathVariable Long roomId,@PathVariable Long voterId,@PathVariable Long voteeId) {
        Room room = roomService.findRoomById(roomId);
        roomService.collectVote(room, voterId, voteeId,roomId);
    }

    @PostMapping("/room/quickStart")
    @ResponseStatus(HttpStatus.OK)
    public RoomGetDTO quickStart(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        Room roomToStart = roomService.findRoomWithMostPlayers();
        roomService.enterRoom(roomToStart, userInput);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(roomToStart);

    }

    @PostMapping("/games/guard")
    @ResponseStatus(HttpStatus.OK)
    public RoomGetDTO playerGuard(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        Room roomToEnter = roomService.findRoomWithThisPlayer(userInput.getId());
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(roomToEnter);
    }


}
