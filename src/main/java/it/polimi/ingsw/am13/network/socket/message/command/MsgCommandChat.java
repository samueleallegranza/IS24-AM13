package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Command message to send a chat message
 */
public class MsgCommandChat extends MsgCommand implements Serializable {
    /**
     * The text of the message
     */
    private final String text;
    /**
     * The receivers of the message
     */
    private final List<PlayerLobby> receivers;

    /**
     * Creates the message by setting the two attributes to the parameters
     * @param receivers of the message
     * @param text of the message
     */
    public MsgCommandChat(List<PlayerLobby> receivers, String text) {
        super();
        this.text = text;
        this.receivers = receivers;
    }

    /**
     *
     * @return the text of the message
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @return the receivers of the message
     */
    public List<PlayerLobby> getReceivers() {
        return receivers;
    }
}
