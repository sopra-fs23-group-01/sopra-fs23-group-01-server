package ch.uzh.ifi.hase.soprafs23.service;


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
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
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
    @Transactional

    //1. mapping: to test the /users add user success-post method success
    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
    }

     //2. mapping: to test the /users add user fail(409 exception)-post method failed
    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();
        testUser2.setUsername("testUsername");
        testUser2.setPassword("testPassword2");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    //3. mapping: Get method success
    @Test
    @Transactional
    public void getUserById_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        User foundUser = userService.userProfileById(createdUser.getId());

        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }
    
    //4. mapping: Get method failed
    @Test
    @Transactional
    public void getUserById_fail() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.userProfileById(1000L));
    }
    
    //5. mapping: post method success
    @Test
    public void test_login_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User loginUser = userService.loginUser(testUser);

        assertEquals(testUser.getUsername(), loginUser.getUsername());
    }

    //6. mapping: put method success
    // @Test
    // public void editUserDifferentUsername_success() {
    //     User testUser = new User();
    //     testUser.setUsername("testUsername");
    //     testUser.setPassword("testPassword");
    //     User oldUser = userService.createUser(testUser);

    //     User newUser = new User();
    //     newUser.setId(oldUser.getId());
    //     System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //     System.out.println(oldUser.getId());
    //     newUser.setUsername("newName");
    //     newUser.setBirthday(new Date());
    //     userService.userEditProfile(newUser);

    //     assertEquals(userRepository.findByUsername("newName").getId(), newUser.getId());
    //     assertEquals(userRepository.findByUsername("newName").getUsername(), newUser.getUsername());

    // }

    //7. mapping: put method success
    @Test
    public void editUser_fail() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User newUser = new User();
        newUser.setId(1000L);
        newUser.setUsername("testUsername");

        assertThrows(ResponseStatusException.class, () -> userService.userEditProfile(newUser));
    }
}

