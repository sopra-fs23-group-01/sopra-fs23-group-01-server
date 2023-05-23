package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStatusTest {

    @Test
    public void testGameStatusEnumValues() {
        assertEquals(GameStatus.ALIVE, GameStatus.valueOf("ALIVE"));
        assertEquals(GameStatus.OUT, GameStatus.valueOf("OUT"));
    }
}
