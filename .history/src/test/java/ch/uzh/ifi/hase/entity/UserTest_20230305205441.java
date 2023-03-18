package ch.uzh.ifi.hase.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        // create a new User object
        User user = new User();
        user.setName("Test User");
        user.setId((long) 1);
        user.setUsername("testuser");
        user.setToken("123456");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("testPassword");
        user.setRegisterDate(new Date());
        user.setBirthday(new Date());

        // test getters
        assertNotNull(user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("testuser", user.getUsername());
        assertEquals("123456", user.getToken());
        assertEquals(UserStatus.ONLINE, user.getStatus());
        assertEquals("testPassword", user.getPassword());
        assertNotNull(user.getRegisterDate());
        assertNotNull(user.getBirthday());

        // test setters
        user.setName("New Test User");
        user.setUsername("newtestuser");
        user.setToken("654321");
        user.setStatus(UserStatus.OFFLINE);
        user.setPassword("newTestPassword");
        user.setRegisterDate(new Date());
        user.setBirthday(new Date());

        assertEquals("New Test User", user.getName());
        assertEquals("newtestuser", user.getUsername());
        assertEquals("654321", user.getToken());
        assertEquals(UserStatus.OFFLINE, user.getStatus());
        assertEquals("newTestPassword", user.getPassword());
        assertNotNull(user.getRegisterDate());
        assertNotNull(user.getBirthday());
    }
}
