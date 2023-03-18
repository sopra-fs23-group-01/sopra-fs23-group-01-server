package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceUserEditProfileTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testUserEditProfile() {
        // Create a test user
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userRepository.save(user);

        // Create a user with updated information
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setUsername("updatedUser");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        // Call the method to be tested
        userService.userEditProfile(updatedUser);

        // Retrieve the updated user from the database
        User retrievedUser = userRepository.getOne(user.getId());

        // Check that the user information has been updated correctly
        assertEquals(updatedUser.getUsername(), retrievedUser.getUsername());
        assertEquals(updatedUser.getBirthday(), retrievedUser.getBirthday());
        assertEquals(user.getPassword(), retrievedUser.getPassword());
    }

    @Test
    public void testUserEditProfileWithNonExistentUser() {
        // Create a user with non-existent ID
        User user = new User();
        user.setId(-1L);

        // Call the method to be tested
        try {
            userService.userEditProfile(user);
            fail("Expected EntityNotFoundException was not thrown");
        } catch (EntityNotFoundException ex) {
            // Check that the exception was thrown with the expected message
            assertTrue(ex.getMessage().contains("Unable to find ch.uzh.ifi.hase.soprafs23.entity.User with id -1"));
        }
    }

    @Test
    public void testUserEditProfileWithDuplicateUsername() {
        // Create two test users with the same username
        User user1 = new User();
        user1.setUsername("testUser");
        user1.setPassword(passwordEncoder.encode("testPassword"));
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("testUser2");
        user2.setPassword(passwordEncoder.encode("testPassword2"));
        user2.setBirthday(LocalDate.of(1990, 1, 1));
        userRepository.save(user2);

        // Attempt to update user2's username to the same as user1's
        user2.setUsername("testUser");

        // Call the method to be tested
        try {
            userService.userEditProfile(user2);
            fail("Expected ResponseStatusException was not thrown");
        } catch (ResponseStatusException ex) {
            // Check that the exception was thrown with the expected status code and message
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertTrue(ex.getMessage().contains("username"));
            assertTrue(ex.getMessage().contains("not unique"));
        }
    }

}
