package it.polimi.ingsw.am13.client.chat;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.io.Serializable;
import java.util.List;

public class ChatMessage{
    private final String text;
    private final PlayerLobby sender;

    public ChatMessage(PlayerLobby sender,String text) {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public PlayerLobby getSender() {
        return sender;
    }
}
