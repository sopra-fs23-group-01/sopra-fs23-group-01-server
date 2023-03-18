package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@SpringBootTest
public class UserServiceUserEditProfileTest {
 
  @Autowired
  private UserService userService;

  @Mock
  private UserRepository userRepository;
  
  @Test
  public void testUserEditProfile() {
    // Set up the test data
    Long userId = 1L;
    String username = "testUser";
    Date birthday = new Date();
    
    User userToUpdate = new User();
    userToUpdate.setId(userId);
    userToUpdate.setUsername(username);
    userToUpdate.setBirthday(birthday);
    
    User mockUser = new User();
    mockUser.setId(userId);
    mockUser.setUsername("existingUser");
    mockUser.setBirthday(new Date(0)); // Set the birthday to the epoch time
    // Set up the mock repository behavior
    when(userRepository.getOne(mockUser.getId())).thenReturn(mockUser);
    when(userRepository.save(mockUser)).thenReturn(mockUser);
    
    // Call the method to be tested
    userService.userEditProfile(userToUpdate);
    
    // Verify the results
    assertEquals(mockUser.getId(), userToUpdate.getId());
    assertEquals(mockUser.getUsername(), userToUpdate.getUsername());
    assertEquals(mockUser.getBirthday(), userToUpdate.getBirthday());
    assertNotNull(mockUser.getLastUpdated());
  }
  
  @Test
  public void testUserEditProfileWithNonexistentUser() {
    // Set up the test data
    Long userId = (long) 1;
    String username = "testUser";
    Date birthday = new Date();
    
    User userToUpdate = new User();
    userToUpdate.setId(userId);
    userToUpdate.setUsername(username);
    userToUpdate.setBirthday(birthday);
    
    // Set up the mock repository behavior
    when(userRepository.getOne(userId)).thenReturn(null);
    
    // Call the method to be tested
    try {
      userService.userEditProfile(userToUpdate);
    } catch (ResponseStatusException ex) {
      assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
      assertTrue(ex.getMessage().contains("not exist"));
    }
  }
  
  @Test
  public void testUserEditProfileWithDuplicateUsername() {
    // Set up the test data
    Long userId = 1L;
    String username = "testUser";
    Date birthday = new Date();
    
    User userToUpdate = new User();
    userToUpdate.setId(userId);
    userToUpdate.setUsername(username);
    userToUpdate.setBirthday(birthday);
    
    User existingUser = new User();
    existingUser.setId(2L);
    existingUser.setUsername(username);
    
    // Set up the mock repository behavior
    when(userRepository.getOne(userId)).thenReturn(existingUser);
    when(userRepository.findByUsername(username)).thenReturn(existingUser);
    
    // Call the method to be tested
    try {
      userService.userEditProfile(userToUpdate);
    } catch (ResponseStatusException ex) {
      assertEquals(HttpStatus.CONFLICT, ex.getStatus());
      assertTrue(ex.getMessage().contains("not unique"));
      assertTrue(ex.getMessage().contains("username"));
    }
  }

private void assertTrue(boolean contains) {
}
}
