package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Menu item for sending a message to the chatroom currently active.
 * It uses the arguments string as the text of the message
 */
public class MenuItemSendChatMessage extends MenuItem{

    /**
     *  List of receivers defining the current shown chat room.
     */
    private final List<PlayerLobby> chatRoom;

    /**
     * Build a new menu item
     */
    public MenuItemSendChatMessage(List<PlayerLobby> chatRoom) {
        super("send",
                "Send a message to this chat room (use enter to send)");
        this.chatRoom = chatRoom;
    }

    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        networkHandler.sendChatMessage(chatRoom, argsStr);
    }
}
