package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.constant.Theme;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public RoomService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("roomRepository") RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<Room> getRooms() {return this.roomRepository.findAll();}

    //Here we create a new room and we need to set the room property and theme according to the input from client
    public Room createRoom(Room newRoom) {
        //newRoom.setToken(UUID.randomUUID().toString());
        newRoom.setRoomOwnerId(newRoom.getRoomOwnerId());
        newRoom.setRoomProperty(newRoom.getRoomProperty());
        newRoom.setTheme(newRoom.getTheme());
        newRoom.addRoomPlayer(userRepository.findById(newRoom.getRoomOwnerId()));
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
        Optional<User> userToAdd = userRepository.findById(user.getId());
        room.addRoomPlayer(userToAdd);
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

//    void checkIfUserExists(User userToBeCreated) {
//        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
//
//        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
//        if (userByUsername != null && userToBeCreated.getId()!= userByUsername.getId()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT,
//                    String.format(baseErrorMessage, "username", "is"));
//        }
//    }
//
//    public User loginUser(User user) {
//        user = checkIfPasswordWrong(user);
//        user.setStatus(UserStatus.ONLINE);
//        user.setToken(UUID.randomUUID().toString());
//
//        return user;
//    }
//
//    User checkIfPasswordWrong(User userToBeLoggedIn) {
//
//        User userByUsername = userRepository.findByUsername(userToBeLoggedIn.getUsername());
//
//        if (userByUsername == null) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username not exist!");
//        }
//        else if (!userByUsername.getPassword().equals(userToBeLoggedIn.getPassword())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password incorrect!");
//        }
//        else {
//            return userByUsername;
//        }
//    }
//
//    //Define the logout function to set the status to OFFLINE when log out
//    public User logoutUser(User userToBeLoggedOut) {
//        User userByUsername = userRepository.getOne(userToBeLoggedOut.getId());
//        userByUsername.setStatus(UserStatus.OFFLINE);
//        return userByUsername;
//    }
//
//    public User userProfileById(Long id) {
//        Optional<User> userByUserid = userRepository.findById(id);
//        if (userByUserid.isPresent()) {
//            return userByUserid.get();
//        }
//        else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this ID:"+id+" not found!");
//        }
//    }
//
//    public void userEditProfile(User user) {
//        if(!userRepository.existsById(user.getId())) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user ID was not found");
//        }
//        User userByUserid = userRepository.getOne(user.getId());
//
//        if(user.getUsername()!=null){
//            checkIfUserExists(user);
//            userByUserid.setUsername(user.getUsername());
//        };
//        // set the birthday
//        if(user.getBirthday()!=null){
//            userByUserid.setBirthday(user.getBirthday());
//        };
//        // set the gender
//        if(user.getGender()!=null){
//            userByUserid.setGender(user.getGender());
//        };
//        // set the email
//        if(user.getEmail()!=null){
//            userByUserid.setEmail(user.getEmail());
//        };
//        // set the intro
//        if(user.getIntro()!=null){
//            userByUserid.setIntro(user.getIntro());
//        };
//        if(user.getAvatarUrl()!=null){
//            userByUserid.setAvatarUrl(user.getAvatarUrl());
//        };
//
//
//        // saves the given entity but data is only persisted in the database once
//        // flush() is called
//        userRepository.flush();
//        // return userByUserid;
//    }
}
