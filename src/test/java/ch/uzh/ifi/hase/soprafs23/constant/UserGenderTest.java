package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserGenderTest {

    @Test
    public void testUserGenderEnumValues() {
        assertEquals(UserGender.MALE, UserGender.valueOf("MALE"));
        assertEquals(UserGender.FEMALE, UserGender.valueOf("FEMALE"));
    }
}
