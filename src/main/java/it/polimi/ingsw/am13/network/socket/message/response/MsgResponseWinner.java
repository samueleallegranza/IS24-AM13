package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message containing the list of players, one of which has won the game.
 * This message is sent when the last turn is over (or only a player is left and the reconnection timer expired)
 */
public class MsgResponseWinner extends MsgResponse implements Serializable {
    /**
     * The list of players of the game, one of which has won the game
     */
    private final List<PlayerLobby> players;

    /**
     * Builds a new response message with the given list of players
     * @param players the list of players of the game, one of which has won the game
     */
    public MsgResponseWinner(List<PlayerLobby> players) {
        super("resWinner");
        this.players = players;
    }

    /**
     * @return the list of players of the game, one of which has won the game
     */
    public List<PlayerLobby> getPlayer() {
        return players;
    }
}
