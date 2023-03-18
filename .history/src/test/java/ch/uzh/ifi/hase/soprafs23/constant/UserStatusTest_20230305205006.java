package ch.uzh.ifi.hase.soprafs23.constant;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserStatusTest {

    @Test
    public void testUserStatusEnumValues() {
        assertEquals(UserStatus.ONLINE, UserStatus.valueOf("ONLINE"));
        assertEquals(UserStatus.OFFLINE, UserStatus.valueOf("OFFLINE"));
    }
}
