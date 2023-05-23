package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadyStatusTest {

    @Test
    public void testReadyStatusEnumValues() {
        assertEquals(ReadyStatus.READY, ReadyStatus.valueOf("READY"));
        assertEquals(ReadyStatus.FREE, ReadyStatus.valueOf("FREE"));
    }
}
