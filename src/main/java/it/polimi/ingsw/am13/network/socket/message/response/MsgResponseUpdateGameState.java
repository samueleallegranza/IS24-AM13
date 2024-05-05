package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.GameModelIF;

public class MsgResponseUpdateGameState extends MsgResponse{
    /**
     * The updated game state
     */
    private GameState gameState;

    public MsgResponseUpdateGameState(String type, GameState gameState) {
        super(type);
        this.gameState = gameState;
    }

    public MsgResponseUpdateGameState(GameModelIF gameModel) {
        super("resUpdateGameState");
        this.gameState = new GameState(gameModel);
    }

    public GameState getGameState() {
        return gameState;
    }
}
