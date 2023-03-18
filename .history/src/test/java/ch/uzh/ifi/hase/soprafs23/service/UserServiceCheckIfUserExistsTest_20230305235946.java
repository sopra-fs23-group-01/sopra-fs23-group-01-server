package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@ExtendWith(SpringExtension.class)
public class UserServiceCheckIfUserExistsTest {
 
 
  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;
  
  @Test
  public void testCheckIfUserExists() {
    // Set up the test data
    String username = "testUser";
    User userToBeCreated = new User();
    userToBeCreated.setUsername(username);
    
    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setUsername(username);
    
    // Set up the mock repository behavior
    when(userRepository.findByUsername(username)).thenReturn(existingUser);
    
    // Call the method to be tested
    try {
      userService.checkIfUserExists(userToBeCreated);
      fail("Expected ResponseStatusException was not thrown");
    } catch (ResponseStatusException ex) {
      assertEquals(HttpStatus.CONFLICT, ex.getStatus());
      assertTrue(ex.getMessage().contains("not unique"));
      assertTrue(ex.getMessage().contains("username"));
    }
  }
}
