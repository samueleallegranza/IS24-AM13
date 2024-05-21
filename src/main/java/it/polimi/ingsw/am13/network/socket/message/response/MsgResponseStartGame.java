package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.GameModelIF;

import java.io.Serializable;

public class MsgResponseStartGame extends MsgResponse implements Serializable {
    /**
     * The game initial game state
     */
    private final GameState gameState;
    public MsgResponseStartGame(GameModelIF modelIF) {
        super("resStartGame");
        this.gameState = new GameState(modelIF);
    }

    public GameState getGameState() {
        return gameState;
    }
}
