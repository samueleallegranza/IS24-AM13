package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponsePlayerDisconnected extends MsgResponse implements Serializable {
    /**
     * The player that has disconnected
     */
    private final PlayerLobby player;

    public MsgResponsePlayerDisconnected(String command, String type, PlayerLobby player) {
        super(command, type);
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
