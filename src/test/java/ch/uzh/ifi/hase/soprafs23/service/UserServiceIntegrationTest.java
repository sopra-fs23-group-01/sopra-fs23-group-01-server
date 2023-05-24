package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User testUser2 = new User();
        testUser2.setUsername("testUsername");
        testUser2.setPassword("testPassword2");

        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void loginUser_validCredentials_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User loginUser = userService.loginUser(testUser);

        assertEquals(testUser.getUsername(), loginUser.getUsername());
        assertNotNull(loginUser.getToken());
        assertEquals(UserStatus.ONLINE, loginUser.getStatus());
    }

    @Test
    public void loginUser_invalidUsername_throwsException() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User invalidUser = new User();
        invalidUser.setUsername("wrongUsername");
        invalidUser.setPassword("testPassword");

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(invalidUser));
    }

    @Test
    public void loginUser_invalidPassword_throwsException() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User invalidUser = new User();
        invalidUser.setUsername("testUsername");
        invalidUser.setPassword("wrongPassword");

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(invalidUser));
    }

    @Test
    public void userProfileById_existingUserId_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        User foundUser = userService.userProfileById(createdUser.getId());

        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    public void userProfileById_nonExistingUserId_throwsException() {
        assertThrows(ResponseStatusException.class, () -> userService.userProfileById(1000L));
    }

    @Test
    public void userEditProfile_existingUser_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User oldUser = userService.createUser(testUser);

        User newUser = new User();
        newUser.setId(oldUser.getId());
        newUser.setUsername("newName");
        newUser.setBirthday(new Date());
        userService.userEditProfile(newUser);

        User updatedUser = userService.userProfileById(newUser.getId());

        assertEquals(newUser.getUsername(), updatedUser.getUsername());
        assertEquals(newUser.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    public void userEditProfile_nonExistingUser_throwsException() {
        User newUser = new User();
        newUser.setId(100000L);
        newUser.setUsername("testUsername1");

        assertThrows(ResponseStatusException.class, () -> userService.userEditProfile(newUser));
    }

    @Test
    public void getUsers_emptyList_success() {
        List<User> users = userService.getUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}

//
