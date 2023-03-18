package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.transaction.Transactional;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserLoginoutTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Before(value = "")
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        userRepository.save(testUser);
    }

    @After(value = "")
    public void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    public void testLoginUser() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        // Act
        User loggedInUser = userService.loginUser(user);

        // Assert
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
        assertNotNull(loggedInUser.getToken());
    }

    @Test
    public void testLogoutUser() {
        // Arrange
        User user = new User();
        user.setId(testUser.getId());

        // Act
        User loggedOutUser = userService.logoutUser(user);

        // Assert
        assertEquals(UserStatus.OFFLINE, loggedOutUser.getStatus());
    }
}

