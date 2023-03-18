package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

@SpringBootTest
public class UserServiceProfileTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  public void testUserProfile() {
    // Setup Test Data
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("JohnDoe");
    mockUser.setPassword("password123");

    // Set up Mock Repository Behaviour
    when(userRepository.getOne(mockUser.getId())).thenReturn(mockUser);

    // Call method to be tested
    User result = userService.userProfile(mockUser);

    // Verify expected results
    assertEquals(mockUser.getId(), result.getId());
    assertEquals(mockUser.getUsername(), result.getUsername());
    assertEquals(mockUser.getPassword(), result.getPassword());
  }

  @Test
  public void testUserProfileNonExistingUser() {
    // Setup Test Data
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("JohnDoe");
    mockUser.setPassword("password123");

    // Set up Mock Repository Behaviour
    when(userRepository.getOne(mockUser.getId())).thenReturn(null);

    // Call method to be tested
    try {
      userService.userProfile(mockUser);
    } catch (ResponseStatusException ex) {
      // Verify expected results
      assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
      assertEquals("User not found", ex.getReason());
    }
  }
}
