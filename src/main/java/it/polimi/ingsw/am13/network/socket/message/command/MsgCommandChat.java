package it.polimi.ingsw.am13.network.socket.message.command;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

public class MsgCommandChat extends MsgCommand implements Serializable {
    private final String text;
    private final List<PlayerLobby> receivers;

    public MsgCommandChat(List<PlayerLobby> receiver, String text) {
        super();
        this.text = text;
        this.receivers = receiver;
    }

    public String getText() {
        return text;
    }

    public List<PlayerLobby> getReceivers() {
        return receivers;
    }
}
