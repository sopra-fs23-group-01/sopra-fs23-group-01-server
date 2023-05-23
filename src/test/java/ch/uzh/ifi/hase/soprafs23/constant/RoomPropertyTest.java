package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomPropertyTest {

    @Test
    public void testRoomPropertyEnumValues() {
        assertEquals(RoomProperty.WAITING, RoomProperty.valueOf("WAITING"));
        assertEquals(RoomProperty.INGAME, RoomProperty.valueOf("INGAME"));
    }
}
