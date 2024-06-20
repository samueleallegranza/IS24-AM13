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

    /**
     * Executes the action this menu item represents
     * @param argsStr        String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If there are no arguments passed via command line
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        if(argsStr.isEmpty())
            throw new InvalidTUICommandException("You must write a message to send");
        networkHandler.sendChatMessage(chatRoom, argsStr);
    }
}
