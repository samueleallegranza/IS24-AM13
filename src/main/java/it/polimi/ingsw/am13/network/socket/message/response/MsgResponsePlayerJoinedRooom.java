package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

/**
 * Response message which is sent when a player successfully joins a room
 */
public class MsgResponsePlayerJoinedRooom extends MsgResponse implements Serializable {
    /**
     * Player who joined the room
     */
    private final PlayerLobby player;

    public MsgResponsePlayerJoinedRooom(PlayerLobby player) {
        super("resPlayerJoinedRoom");
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
