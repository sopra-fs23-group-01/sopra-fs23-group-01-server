package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@SpringBootTest
public class UserServicePasswordTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date(0));
        testUser.setRegisterDate(new Date(0));
        testUser.setToken("testToken");
        testUser.setStatus(UserStatus.ONLINE);
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(testUser);
    }
    //检查相同用户名和用户密码是否返回相同的用户（通过ID判断）
    @Test
    public void testCheckIfPasswordWrong() {
        User userToBeLoggedIn = new User();
        userToBeLoggedIn.setUsername(testUser.getUsername());
        userToBeLoggedIn.setPassword(testUser.getPassword());
        User loggedInUser = userService.checkIfPasswordWrong(userToBeLoggedIn);
        assertEquals(loggedInUser.getId(), testUser.getId());
    }

    //检查错误密码返回值
    @Test
    public void testCheckIfPasswordWrongWithWrongPassword() {
        User userToBeLoggedIn = new User();
        userToBeLoggedIn.setUsername(testUser.getUsername());
        userToBeLoggedIn.setPassword("wrongPassword");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.checkIfPasswordWrong(userToBeLoggedIn);
        });
        assertEquals(exception.getStatus(), HttpStatus.FORBIDDEN);
        assertEquals(exception.getReason(), "Password incorrect!");
    }


    //检查无用户名的情况
    @Test
    public void testCheckIfPasswordWrongWithNonexistentUsername() {
        User userToBeLoggedIn = new User();
        userToBeLoggedIn.setUsername("nonexistentUser");
        userToBeLoggedIn.setPassword("testPassword");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.checkIfPasswordWrong(userToBeLoggedIn);
        });
        assertEquals(exception.getStatus(), HttpStatus.FORBIDDEN);
        assertEquals(exception.getReason(), "Username not exist!");
    }

}