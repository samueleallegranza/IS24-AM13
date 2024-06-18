package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

/**
 * Response message which is sent when a player successfully reconnects to an existing game
 */
public class MsgResponsePlayerReconnected extends MsgResponse implements Serializable {
    /**
     * The player that has reconnected
     */
    private final PlayerLobby player;

    public MsgResponsePlayerReconnected(PlayerLobby player) {
        super("resPlayerReconnected");
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
