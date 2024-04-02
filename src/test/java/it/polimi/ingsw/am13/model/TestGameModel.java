package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameModel {

    @Test
    public void testCreation() throws InvalidPlayersNumberException {
        assertThrows(InvalidParameterException.class, ()->new GameModel(1,
                Arrays.asList("1", "2"),
                List.of(ColorToken.RED)));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                List.of("1"),
                List.of(ColorToken.RED)));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                Arrays.asList("1", "2", "3"),
                Arrays.asList(ColorToken.RED, ColorToken.BLUE, ColorToken.RED)));
        assertThrows(InvalidPlayersNumberException.class, ()->new GameModel(1,
                Arrays.asList("1", "2"),
                Arrays.asList(ColorToken.RED, ColorToken.BLACK)));

        GameModel game = new GameModel(1,
                Arrays.asList("1", "2"),
                Arrays.asList(ColorToken.RED, ColorToken.BLUE));
        assertNull(game.fetchGameStatus());
        assertNull(game.fetchCurrentPlayer());
    }

}