package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message containing the winner of the game which is sent after the last turn has been played
 * (or only a player is left and the reconnection timer expired)
 */
public class MsgResponseWinner extends MsgResponse implements Serializable {
    /**
     * The player that has won the game
     */
    private final List<PlayerLobby> players;

    public MsgResponseWinner(List<PlayerLobby> players) {
        super("resWinner");
        this.players = players;
    }


    public List<PlayerLobby> getPlayer() {
        return players;
    }
}
