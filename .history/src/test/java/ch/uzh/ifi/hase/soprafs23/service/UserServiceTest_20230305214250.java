package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    private User testUser;
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
    void checkIfUserExists_userDoesNotExist_doesNotThrowException() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> userService.checkIfUserExists(user));
    }

    @Test
    void checkIfUserExists_userExistsButIsSameAsInput_doesNotThrowException() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(existingUser);

        // Act & Assert
        assertDoesNotThrow(() -> userService.checkIfUserExists(user));
    }
}