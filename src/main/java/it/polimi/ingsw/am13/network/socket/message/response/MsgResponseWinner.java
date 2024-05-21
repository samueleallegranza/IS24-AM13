package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

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
