package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }
  
  @Test
  public void createUser_duplicateName_throwsException() {
      // given -> a first user has already been created
      userService.createUser(testUser);

      // when -> setup additional mocks for UserRepository
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      // then -> attempt to create second user with same user -> check that an error
      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    User existuser;
    existuser = new User();
    existuser.setId(2L);
    existuser.setUsername("testUsername");
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(existuser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  

 @Test
  public void getUserById_validInput_success(){
    // Mock the userRepository to return the testUser object
    given(userRepository.findById(Mockito.any())).willReturn(java.util.Optional.ofNullable(testUser));
    User user = new User();
    user.setId(testUser.getId());
    // Call the userService method with the testUser's id
    User foundUser = userService.userProfileById(user.getId());

    // Verify that the userService method returns the correct user object
    assertEquals(testUser.getId(), foundUser.getId(), "The id does not match");
    assertEquals(testUser.getUsername(), foundUser.getUsername(), "The username does not match");
    assertEquals(testUser.getToken(), foundUser.getToken(), "The token does not match");
}


  @Test
  public void getUserById_invalidInput_fail() {
      userService.createUser(testUser);

      Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

      assertThrows( ResponseStatusException.class, () -> userService.userProfileById(10L));
  }

  @Test
  public void test_login_success(){
      User user = new User();
      user.setUsername(testUser.getUsername());
      user.setPassword(testUser.getPassword());

      // when -> setup additional mocks for UserRepository
      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      User loginUser = userService.loginUser(user);

      assertEquals(testUser, loginUser);
  }

  @Test
  public void login_fail_username() {
      userService.createUser(testUser);

      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

      assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
  }

  @Test
  public void login_fail_password() {
      userService.createUser(testUser);
      User user = new User();
      user.setUsername(testUser.getUsername());
      user.setPassword("wrong password");

      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }


}
