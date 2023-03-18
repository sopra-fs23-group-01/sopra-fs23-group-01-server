package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        // Given
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        userList.add(user);

        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<User> result = userService.getUsers();

        // Then
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
    }

    @Test
    public void testCreateUser() {
        // Given
        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("testUser");
        newUser.setPassword("testPassword");
        newUser.setBirthday(new Date());

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(null);
        when(userRepository.save(newUser)).thenReturn(newUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(newUser.getUsername(), result.getUsername());
        assertEquals(newUser.getPassword(), result.getPassword());
        assertNotNull(result.getToken());
        assertEquals(UserStatus.ONLINE, result.getStatus());
        assertNotNull(result.getRegisterDate());
    }

    @Test
    public void testCreateUserWithExistingUsername() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testUser");
        existingUser.setPassword("testPassword");
        existingUser.setBirthday(new Date());

        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("testUser");
        newUser.setPassword("testPassword");
        newUser.setBirthday(new Date());

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(existingUser);

        // Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.createUser(newUser);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("The username provided is not unique. Therefore, the user could not be created!", exception.getReason());
    }

    @Test
    public void testLoginUserWithCorrectPassword() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setStatus(UserStatus.OFFLINE);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("testPassword");

        // When
        User result = userService.loginUser(loginUser);

        // Then
        assertNotNull(result.getToken());
        assertEquals(UserStatus.ONLINE, result.getStatus());
    }

    @Test
    public void testLoginUserWithWrongPassword() {
        //
