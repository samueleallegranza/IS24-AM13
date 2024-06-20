package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;

/**
 * Response message that is sent when a player disconnects from an ongoing game
 */
public class MsgResponsePlayerDisconnected extends MsgResponse implements Serializable {
    /**
     * The player that has disconnected
     */
    private final PlayerLobby player;

    /**
     * Builds a new response message with the given player
     * @param player the player that has disconnected
     */
    public MsgResponsePlayerDisconnected(PlayerLobby player) {
        super("resPlayerDisconnected");
        this.player = player;
    }

    /**
     * @return the player that has disconnected
     */
    public PlayerLobby getPlayer() {
        return player;
    }
}
