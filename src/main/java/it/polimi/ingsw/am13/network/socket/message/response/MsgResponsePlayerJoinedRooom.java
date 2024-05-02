package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponsePlayerJoinedRooom extends MsgResponse implements Serializable {
    /**
     * Player who joined the room
     */
    private final PlayerLobby player;

    public MsgResponsePlayerJoinedRooom(String command, String type, PlayerLobby player) {
        super(command, type);
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
