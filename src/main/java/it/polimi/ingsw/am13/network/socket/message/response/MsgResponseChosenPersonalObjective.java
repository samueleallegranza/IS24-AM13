package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponseChosenPersonalObjective extends MsgResponse implements Serializable {
    /**
     * The player that has chosen the personal objective
     */
    private final PlayerLobby player;

    public MsgResponseChosenPersonalObjective(String command, String type, PlayerLobby player) {
        super(command, type);
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
