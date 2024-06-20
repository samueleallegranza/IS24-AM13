package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.ViewTUI;

/**
 * Menu item for leaving the chat room currently active.
 * It does not need any argument
 */
public class MenuItemLeaveChat extends MenuItem {

    /**
     * View handling the printed match for TUI
     */
    private final ViewTUI view;

    /**
     * Build a new menu item
     */
    public MenuItemLeaveChat(ViewTUI view) {
        super("leave",
                "Leave the chat-room: 'leave'");
        this.view = view;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr        String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If there are argument passed via command line
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        if(!argsStr.isEmpty())
            throw new InvalidTUICommandException("Leaving the chat does not require any other argument");
        view.leaveChatRoom();
    }
}
