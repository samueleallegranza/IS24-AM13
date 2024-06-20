package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

/**
 * Menu item for leaving the joined room.
 * It simply sends to the server the command for leaving the room
 */
public class MenuItemLeaveRoom extends MenuItem {

    //TODO da implementare (Federico: potrei averlo fatto)
    /**
     * Build a new item for leaving the room
     */
    public MenuItemLeaveRoom() {
        super("leave",
                "Leave the room you joined (if you are the only one remained, you will destroy the room");
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
            throw new InvalidTUICommandException("Leaving the room does not require any other argument");
        networkHandler.leaveRoom();
    }
}
