package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponsePlayerLeftRoom extends MsgResponse implements Serializable {
    /**
     * Player who left the room
     */
    private final PlayerLobby player;

    public MsgResponsePlayerLeftRoom(String command, String type, PlayerLobby player) {
        super(command, type);
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
