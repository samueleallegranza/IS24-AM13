package it.polimi.ingsw.am13.model;

import it.polimi.ingsw.am13.model.exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Player;
import it.polimi.ingsw.am13.model.player.PlayerIF;
import it.polimi.ingsw.am13.model.player.Token;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class GameModel {

    /**
     * Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    private final int gameId;
    /**
     * Class match with all the information regarding the match itself and how to precess it
     */
    private final Match match;

    /**
     * Creates a new instance of <code>GameModel</code> with the specified players.
     * The players used here to create the model are the definitive players, and nobody can be added in a second time.
     * @param gameId Class match with all the information regarding the match itself and how to precess it
     * @param nicks List of nicknames for the players
     * @param colors List of colors chosen for the players, with same order as the players
     * @throws InvalidParameterException If the lists nicks, colors don't have the same size
     * @throws InvalidPlayersNumberException If lists nicks, colors have size <2 or >4, or one of the colors is black,
     * or there are duplicate chosen colors
     */
    public GameModel(int gameId, List<String> nicks, List<ColorToken> colors)
            throws InvalidParameterException, InvalidPlayersNumberException {
        this.gameId = gameId;
        if(nicks.size()!=colors.size())
            throw new InvalidParameterException("Parameters nicks, colors must have the same size");
        List<Player> players = new ArrayList<>();
        for(int i=0 ; i<nicks.size() ; i++)
            players.add(new Player(nicks.get(i), new Token(colors.get(i))));
        this.match = new Match(players);
    }

    /**
     * @return Unique number indicating the actual game model (hence the game/match to its all entirety)
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * @return the player who is playing in the current turn.
     * Null if the game phase is different from IN_GAME or FINAL_PHASE (the 2 phases divided in turns)
     */
    public PlayerIF fetchCurrentPlayer() {
        return match.getCurrentPlayer();
    }

    /**
     * @return the status of the game. See class <code>GameStatus</code> for more details
     */
    public GameStatus fetchGameStatus() {
        return match.getGameStatus();
    }
}
