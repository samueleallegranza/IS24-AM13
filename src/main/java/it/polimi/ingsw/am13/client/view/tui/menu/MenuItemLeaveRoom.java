package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

/**
 * Menu item for leaving the joined room.
 * It simply sends to the server the command for leaving the room
 */
public class MenuItemLeaveRoom extends MenuItem {

    //TODO da implementare

    public MenuItemLeaveRoom() {
        super("leave",
                "Leave the room you joined (if you are the only one remained, you will destroy the room");
    }

    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        networkHandler.leaveRoom();
    }
}
