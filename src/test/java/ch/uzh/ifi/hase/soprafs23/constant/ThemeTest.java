package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThemeTest {

    @Test
    public void testThemeEnumValues() {
        assertEquals(Theme.SPORTS, Theme.valueOf("SPORTS"));
        assertEquals(Theme.FURNITURE, Theme.valueOf("FURNITURE"));
        assertEquals(Theme.JOB, Theme.valueOf("JOB"));
    }
}
