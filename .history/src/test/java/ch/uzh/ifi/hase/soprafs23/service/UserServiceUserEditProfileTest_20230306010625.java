package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

  @Test
  public void testUserEditProfile() {
    // Create a user to be edited
    User userToBeEdited = new User();
    userToBeEdited.setUsername("testUser");
    userToBeEdited.setPassword("testPassword");
    userToBeEdited.setBirthday(LocalDate.of(2000, 1, 1));
    userRepository.saveAndFlush(userToBeEdited);

    // Edit the user's username
    User editedUser = new User();
    editedUser.setId(userToBeEdited.getId());
    editedUser.setUsername("newUsername");
    userService.userEditProfile(editedUser);

    // Verify that the user's username was updated in the database
    User savedUser = userRepository.getOne(userToBeEdited.getId());
    assertEquals("newUsername", savedUser.getUsername());
    assertEquals("testPassword", savedUser.getPassword());
    assertEquals(LocalDate.of(2000, 1, 1), savedUser.getBirthday());

    // Edit the user's birthday
    editedUser.setBirthday(LocalDate.of(1990, 1, 1));
    userService.userEditProfile(editedUser);

    // Verify that the user's birthday was updated in the database
    savedUser = userRepository.getOne(userToBeEdited.getId());
    assertEquals("newUsername", savedUser.getUsername());
    assertEquals("testPassword", savedUser.getPassword());
    assertEquals(LocalDate.of(1990, 1, 1), savedUser.getBirthday());

    // Try to edit the user's username to an already existing username
    User existingUser = new User();
    existingUser.setUsername("existingUser");
    existingUser.setPassword("testPassword");
    userRepository.saveAndFlush(existingUser);

    editedUser.setUsername("existingUser");
    assertThrows(ResponseStatusException.class, () -> userService.userEditProfile(editedUser));

    // Try to edit the user's username to null
    editedUser.setUsername(null);
    assertThrows(ResponseStatusException.class, () -> userService.userEditProfile(editedUser));


    // Verify that the user's birthday was set to null in the database
    savedUser = userRepository.getOne(userToBeEdited.getId());
    assertEquals("newUsername", savedUser.getUsername());
    assertEquals("testPassword", savedUser.getPassword());
    assertNull(savedUser.getBirthday());
  }

}
