package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class PlayerController {
    @GetMapping("/games/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayers(@PathVariable("roomId") Long roomId) {
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

    //when user enter the room, it transfers this user to a player
    @PostMapping("/games/room/{roomId}/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerGetDTO enterRoom(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId) {
//        // convert API user to internal representation
//        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
//
//        // create user
//        User createdUser = userService.createUser(userInput);
//        // convert internal representation of user back to API
//        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
        return null;
    }

    //when player quit the room, it deletes this player
    @DeleteMapping ("/games/room/{roomId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deletePlayer(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId){

    }

    //Change the Ready status
    @PutMapping  ("/games/room/{roomId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void setReady(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId){

    }

    @PostMapping  ("/games/room/{roomId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void startGame(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId){

    }

}
