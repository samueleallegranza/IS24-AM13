package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

/**
 * Response message which is sent to the player who just reconnected to an already started match, containing
 * the game state
 */
public class MsgResponseUpdateGameState extends MsgResponse{
    /**
     * The updated game state
     */
    private final GameState gameState;

    /**
     * Player who wants to reconnect to an already started match
     */
    private final PlayerLobby player;

    public MsgResponseUpdateGameState(GameModelIF gameModel, PlayerLobby player) {
        super("resUpdateGameState");
        this.gameState = new GameState(gameModel);
        this.player = player;
    }

    public GameState getGameState() {
        return gameState;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
