package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

/**
 * Response message which is sent when a player successfully leaves a game
 */
public class MsgResponsePlayerLeftRoom extends MsgResponse implements Serializable {
    /**
     * Player who left the room
     */
    private final PlayerLobby player;

    public MsgResponsePlayerLeftRoom(PlayerLobby player) {
        super("resPlayerLeftRoom");
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
