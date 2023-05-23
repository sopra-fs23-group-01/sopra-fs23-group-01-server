package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {

    @Test
    public void testRoleEnumValues() {
        assertEquals(Role.UNDERCOVER, Role.valueOf("UNDERCOVER"));
        assertEquals(Role.DETECTIVE, Role.valueOf("DETECTIVE"));
        assertEquals(Role.NOT_ASSIGNED, Role.valueOf("NOT_ASSIGNED"));
    }
}
