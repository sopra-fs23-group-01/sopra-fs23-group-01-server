package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Autowired
    private UserService userService;

    @Test
    public void testCreateUser() {
        //创建一个用户
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("testpassword");

        //创建用户
        User createdUser = userService.createUser(newUser);

        //验证用户是否创建成功
        assertNotNull(createdUser.getId());
        assertEquals(newUser.getUsername(), createdUser.getUsername());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }



    //test getusers,add two users and to count the final numbers 
    @Test
    public void testGetUsers() {
    // Setup
    User user1 = new User();
    user1.setUsername("user1");
    user1.setPassword("password1");
    userService.createUser(user1);

    User user2 = new User();
    user2.setUsername("user2");
    user2.setPassword("password2");
    userService.createUser(user2);

    // Execute
    List<User> users = userService.getUsers();

    // Assert
    assertEquals(2, users.size());
}

}