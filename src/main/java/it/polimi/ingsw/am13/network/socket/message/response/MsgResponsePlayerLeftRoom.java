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

    /**
     * Builds a new response message with the given player
     * @param player the player who left the room
     */
    public MsgResponsePlayerLeftRoom(PlayerLobby player) {
        super("resPlayerLeftRoom");
        this.player = player;
    }

    /**
     * @return the player who left the room
     */
    public PlayerLobby getPlayer() {
        return player;
    }
}
