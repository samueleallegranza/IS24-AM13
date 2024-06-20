package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message containing the message that should be sent to its receivers (including its sender and receivers)
 */
public class MsgResponseChat extends MsgResponse implements Serializable{
    /**
     * The content of the message that should be sent
     */
    private final String text;

    /**
     * The sender of the message
     */
    private final PlayerLobby sender;

    /**
     * The receivers of the message
     */
    private final List<PlayerLobby> receivers;

    /**
     * Builds a new response message with the given sender, receivers and text
     * @param sender the sender of the message
     * @param receivers the receivers of the message
     * @param text the content of the message
     */
    public MsgResponseChat(PlayerLobby sender, List<PlayerLobby> receivers,String text) {
        super("chat");
        this.text = text;
        this.sender = sender;
        this.receivers = receivers;
    }

    /**
     * @return the content of the message
     */
    public String getText() {
        return text;
    }

    /**
     * @return the sender of the message
     */
    public PlayerLobby getSender() {
        return sender;
    }

    /**
     * @return the receivers of the message
     */
    public List<PlayerLobby> getReceivers() {
        return receivers;
    }
}
