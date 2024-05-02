package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.client.GameState;
import it.polimi.ingsw.am13.model.GameModelIF;

import java.io.Serializable;

public class MsgResponseStartGame extends MsgResponse implements Serializable {
    /**
     * The game initial game state
     */
    private final GameState gameState;
    public MsgResponseStartGame(String command, String type, GameState gameState) {
        super(command, type);
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
