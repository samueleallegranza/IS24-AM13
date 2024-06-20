package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.GameModelIF;

import java.io.Serializable;

/**
 * Response message which is sent when the game starts, containing the game state at that moment
 */
public class MsgResponseStartGame extends MsgResponse implements Serializable {
    /**
     * The game initial game state
     */
    private final GameState gameState;

    /**
     * Builds a new response message with the given game state
     * @param modelIF the game model
     */
    public MsgResponseStartGame(GameModelIF modelIF) {
        super("resStartGame");
        this.gameState = new GameState(modelIF);
    }

    /**
     * @return the game state at the moment the game started
     */
    public GameState getGameState() {
        return gameState;
    }
}
