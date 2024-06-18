package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

/**
 * Response message containing the winner of the game which is sent after the last turn has been played
 * (or only a player is left and the reconnection timer expired)
 */
public class MsgResponseWinner extends MsgResponse implements Serializable {
    /**
     * The player that has won the game
     */
    private final PlayerLobby player;

    public MsgResponseWinner(PlayerLobby player) {
        super("resWinner");
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
