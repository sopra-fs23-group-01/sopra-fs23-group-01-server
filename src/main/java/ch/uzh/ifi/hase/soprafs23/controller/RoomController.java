package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomDeleteDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

public class RoomController {

    //display all rooms in game lobby
    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoomGetDTO> getAllRooms() {
        // fetch all users in the internal representation，
//        List<User> users = userService.getUsers();
//        List<UserGetDTO> userGetDTOs = new ArrayList<>();
//
//        // convert each user to the API representation
//        for (User user : users) {
//            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
//        }
        //return userGetDTOs;
        return null;
    }

    //create a new room
    @PostMapping("/games/room/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomGetDTO createRoom(@RequestBody RoomPostDTO roomPostDTO, @PathVariable("userId") Long userId) {
//        // convert API user to internal representation
//        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
//
//        // create user
//        User createdUser = userService.createUser(userInput);
//        // convert internal representation of user back to API
//        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
        return null;
    }

    //when the owner quit the room, deletes the room
    @DeleteMapping ("/games/room/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteRoom(@PathVariable("roomId") Long roomId){

    }






}
