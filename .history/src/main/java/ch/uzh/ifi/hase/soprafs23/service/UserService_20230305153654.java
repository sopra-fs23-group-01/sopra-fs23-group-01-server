package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
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
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
    newUser.setRegisterDate(formatter.format(date));
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
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


  //只检查名字是否重复
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userToBeCreated.getId()!= userByUsername.getId()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format(baseErrorMessage, "username", "is"));
    }
}

public User loginUser(User user) {
  user = checkIfPasswordWrong(user);
  user.setStatus(UserStatus.ONLINE);
  user.setToken(UUID.randomUUID().toString());

  return user;
}

//增加登出函数将server端状态改为offline
public User logoutUser(User userToBeLoggedOut) {
  User userByUsername = userRepository.getOne(userToBeLoggedOut.getId());
  userByUsername.setStatus(UserStatus.OFFLINE);
  return userByUsername;
}

private User checkIfPasswordWrong(User userToBeLoggedIn) {
  //1.先利用user.java得到请求user的name

  User userByUsername = userRepository.findByUsername(userToBeLoggedIn.getUsername());

  if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username not exist!");
  }
  else if (!userByUsername.getPassword().equals(userToBeLoggedIn.getPassword())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password incorrect!");
  }
  else {
      return userByUsername;
  }
}

public User userProfile(User user) {
  User userByUserid = userRepository.getOne(user.getId());
  return userByUserid;
}


public void userEditProfile(User user) {
  User userByUserid = userRepository.getOne(user.getId());
  checkIfUserExists(user);
  if(user.getUsername()!=null){
    userByUserid.setUsername(user.getUsername());
  }
  //date值输入数据库

  
  if(user.getBirthday()!=null){
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String formattedDate = sdf.format(user.getBirthday());
    //userByUserid.setBirthday(formattedDate );
  throw new ResponseStatusException(HttpStatus.OK, String.format(formattedDate.toString(), "username", "is"));
  }

  // saves the given entity but data is only persisted in the database once
  // flush() is called
  userRepository.flush();
  // return userByUserid;
}
}
