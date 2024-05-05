package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponseNextTurn extends MsgResponse implements Serializable {
    /**
     * The player that has to play the next turn
     */
    private final PlayerLobby player;

    public MsgResponseNextTurn(PlayerLobby player) {
        super("resNextTurn");
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
