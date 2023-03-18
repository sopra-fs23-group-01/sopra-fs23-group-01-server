package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

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

        // when -> any object is being saved in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being saved in the userRepository -> return the dummy
        // testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
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
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
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

    @Test
    public void editUsername_success(){
        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("testUsername");

        // when -> any object is being updated in the userRepository -> return the testUpdateUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(newUser);
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testUser));

        // when -> any object is being saved in the userRepository -> return the testUser
        userService.editUser(newUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), newUser.getId());
        assertEquals(testUser.getUsername(), newUser.getUsername());
    }

    @Test
    public void editUserDifferentUsername_success(){
        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("newName");
        newUser.setBirthday(new Date());

        // when -> any object is being updated in the userRepository -> return the testUpdateUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(newUser);
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testUser));



        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), newUser.getId());
        assertEquals(testUser.getUsername(), newUser.getUsername());
        assertEquals(testUser.getBirthday(), newUser.getBirthday());
    }
}