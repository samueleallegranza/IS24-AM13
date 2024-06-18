package it.polimi.ingsw.am13.network.socket.message.response;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

/**
 * Response message containing the message that should be sent to its receivers (including its sender and receivers)
 */
public class MsgResponseChat extends MsgResponse implements Serializable{
    private final String text;
    private final PlayerLobby sender;
    private final List<PlayerLobby> receivers;

    public MsgResponseChat(PlayerLobby sender, List<PlayerLobby> receivers,String text) {
        super("chat");
        this.text = text;
        this.sender = sender;
        this.receivers = receivers;
    }

    public String getText() {
        return text;
    }

    public PlayerLobby getSender() {
        return sender;
    }

    public List<PlayerLobby> getReceivers() {
        return receivers;
    }
}
