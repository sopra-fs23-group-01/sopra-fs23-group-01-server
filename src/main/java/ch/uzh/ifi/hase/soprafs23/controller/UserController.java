package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
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
public class UserController {

  private final UserService userService;
  private final RoomService roomService;

  UserController(UserService userService, RoomService roomService) {
    this.userService = userService;
      this.roomService = roomService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody


  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation，
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

    @GetMapping("/games/playerList/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    //similar to above GET method, change all users to users in the room
    public List<UserGetDTO> getRoomPlayers(@PathVariable("roomId") Long roomId) {
    Room room = roomService.findRoomById(roomId);
    List<UserGetDTO> playerGetDTOs = new ArrayList<>();

    for (Long id: room.getRoomPlayersList()){
        playerGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUserById(id)));
    }
    return playerGetDTOs;
    }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

//login
  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
      // convert API user to internal representation
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      
      User loggedInUser = userService.loginUser(userInput);

      // convert internal representation of user back to API
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);
  }

//logout
@PostMapping("/users/logout")
@ResponseStatus(HttpStatus.OK)
@ResponseBody
public UserGetDTO logoutUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    User loggedOutUser = userService.logoutUser(userInput);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedOutUser);
}


//Get method for getting the information for profile page
@GetMapping("/users/{userId}")
@ResponseStatus(HttpStatus.OK)
@ResponseBody
public UserGetDTO userProfile (@PathVariable("userId") Long userId) {
  User user = userService.userProfileById(userId);
  return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
}


//Put method for editing the user profile
@PutMapping("/users/{userId}")
@ResponseStatus(HttpStatus.OK)
@ResponseBody
public void userEditProfile(@PathVariable("userId") Long userId, @RequestBody UserPutDTO userPutDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    userService.userEditProfile(userInput);
}

    @PutMapping("/users/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void userGetReady(@PathVariable("roomId") Long roomId, @RequestBody UserPutDTO userPutDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        userService.userSetReady(userInput);
    }

    @PutMapping("/users/room/out/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void userLeaveRoom(@PathVariable("roomId") Long roomId, @RequestBody UserPutDTO userPutDTO) {
        //To change the readyStatus of users to be FREE;
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        userService.userLeaveRoom(userInput);
        roomService.deletePlayer(userInput.getId(), roomId);
    }


}
