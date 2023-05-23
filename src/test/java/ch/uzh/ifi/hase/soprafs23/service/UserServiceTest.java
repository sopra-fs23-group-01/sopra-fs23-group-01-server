package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ReadyStatus;
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
import static org.mockito.Mockito.when;

import java.util.Date;
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
    when(userRepository.save(Mockito.any())).thenReturn(testUser);
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
      User testUser2 = new User();
      //set the same name but different password
      testUser2.setUsername("testUsername");
      testUser2.setPassword("testPassword2");

      userService.createUser(testUser);
      // when -> setup additional mocks for UserRepository
      when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      // then -> attempt to create second user with same user -> check that an error
      // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
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
    when(userRepository.findByUsername(Mockito.any())).thenReturn(existuser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  

 @Test
  public void getUserById_validInput_success(){
    // Mock the userRepository to return the testUser object
    given(userRepository.findById(Mockito.any())).willReturn(java.util.Optional.ofNullable(testUser));
    
    //simulate the input
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

      when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

      assertThrows( ResponseStatusException.class, () -> userService.userProfileById(10L));
  }

  @Test
  public void test_login_success(){
      User user = new User();
      user.setUsername(testUser.getUsername());
      user.setPassword(testUser.getPassword());

      // when -> setup additional mocks for UserRepository
      when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      User loginUser = userService.loginUser(user);

      assertEquals(testUser, loginUser);
  }

  @Test
  public void login_fail_username() {
      userService.createUser(testUser);

      when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

      assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
  }

  @Test
  public void login_fail_password() {
      userService.createUser(testUser);
      User user = new User();
      user.setUsername(testUser.getUsername());
      user.setPassword("wrong password");

      when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      assertThrows(ResponseStatusException.class, () -> userService.loginUser(user));
  }

  @Test
  public void editUsername_Birthday_success(){
      User newUser = new User();
      newUser.setId(1L);
      newUser.setUsername("testUsername");
      newUser.setBirthday(new Date());
      userService.createUser(newUser);

      // when -> any object is being updated in the userRepository -> return the testUpdateUser
      when(userRepository.save(Mockito.any())).thenReturn(newUser);
      when(userRepository.existsById(Mockito.any())).thenReturn(true);
      when(userRepository.getOne(Mockito.any())).thenReturn(testUser);

      // when -> any object is being saved in the userRepository -> return the testUser
      userService.userEditProfile(newUser);

      // then
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

      assertEquals(testUser.getId(), newUser.getId());
      assertEquals(testUser.getUsername(), newUser.getUsername());
      assertEquals(testUser.getBirthday(), newUser.getBirthday());
  }

    @Test
    public void userLeaveRoom_ValidUser_Success() {
        // 准备数据
        User user = new User();
        user.setId(1L);
        user.setReadyStatus(ReadyStatus.READY);

        // 模拟 userRepository 的行为
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.getOne(user.getId())).thenReturn(user);

        // 调用被测试方法
        userService.userLeaveRoom(user);

        // 验证状态是否正确更新
        assertEquals(ReadyStatus.FREE, user.getReadyStatus());
    }

    @Test
    public void userLeaveRoom_InvalidUser_NotFoundException() {
        // 准备数据
        User user = new User();
        user.setId(1L);

        // 模拟 userRepository 的行为
        when(userRepository.existsById(user.getId())).thenReturn(false);

        // 调用被测试方法并断言抛出预期的异常
        assertThrows(NullPointerException.class, () -> {
            userService.userLeaveRoom(user);
        });

    }

    @Test
    public void userSetReady_UserFreeStatus_SetToReadyStatus() {
        // 准备数据
        User user = new User();
        user.setId(1L);
        user.setReadyStatus(ReadyStatus.FREE);

        // 模拟 userRepository 的行为
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.getOne(user.getId())).thenReturn(user);

        // 调用被测试方法
        userService.userSetReady(user);

        // 验证状态是否正确更新
        assertEquals(ReadyStatus.READY, user.getReadyStatus());
    }

    @Test
    public void userSetReady_UserReadyStatus_SetToFreeStatus() {
        // 准备数据
        User user = new User();
        user.setId(1L);
        user.setReadyStatus(ReadyStatus.READY);

        // 模拟 userRepository 的行为
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.getOne(user.getId())).thenReturn(user);

        // 调用被测试方法
        userService.userSetReady(user);

        // 验证状态是否正确更新
        assertEquals(ReadyStatus.FREE, user.getReadyStatus());

    }

    @Test
    public void userProfileById_ValidId_Success() {
        // 准备数据
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("exampleUser");

        // 模拟 userRepository 的行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // 调用被测试方法
        User result = userService.userProfileById(userId);

        // 验证返回的结果是否正确
        assertEquals(user, result);

    }

    @Test
    public void userProfileById_InvalidId_NotFoundException() {
        // 准备数据
        Long userId = 1L;

        // 模拟 userRepository 的行为
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // 调用被测试方法并断言抛出预期的异常
        assertThrows(ResponseStatusException.class, () -> {
            userService.userProfileById(userId);
        });

    }


}
