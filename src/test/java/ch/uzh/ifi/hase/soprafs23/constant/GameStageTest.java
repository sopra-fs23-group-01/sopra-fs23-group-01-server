package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStageTest {

    @Test
    public void testGameStageEnumValues() {
        assertEquals(GameStage.DESCRIPTION, GameStage.valueOf("DESCRIPTION"));
        assertEquals(GameStage.VOTING, GameStage.valueOf("VOTING"));
        assertEquals(GameStage.END, GameStage.valueOf("END"));
        assertEquals(GameStage.WAITING, GameStage.valueOf("WAITING"));
    }
}
