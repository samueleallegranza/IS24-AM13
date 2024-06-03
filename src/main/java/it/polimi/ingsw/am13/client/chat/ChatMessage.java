package it.polimi.ingsw.am13.client.chat;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

/**
 * A single chat message, as it is stored on the client. The receivers are not stored explicitly in the message
 * because of the ChatMessage are used in Chat.
 */
public class ChatMessage{
    /**
     * The text (content) of the message
     */
    private final String text;
    /**
     * The sender of the message
     */
    private final PlayerLobby sender;

    /**
     * Sets the attributes to the corresponding parameter
     * @param sender of the message
     * @param text of the message
     */
    public ChatMessage(PlayerLobby sender,String text) {
        this.text = text;
        this.sender = sender;
    }

    /**
     * @return the text of the message
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
}
