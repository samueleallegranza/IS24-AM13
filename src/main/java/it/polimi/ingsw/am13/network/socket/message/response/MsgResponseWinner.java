package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

public class MsgResponseWinner extends MsgResponse implements Serializable {
    /**
     * The player that has won the game
     */
    private final PlayerLobby player;

    public MsgResponseWinner(String command, String type, PlayerLobby player) {
        super(command, type);
        this.player = player;
    }

    public PlayerLobby getPlayer() {
        return player;
    }
}
