package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

public class MenuItemLeaveRoom extends MenuItem {

    //TODO da implementare

    /**
     * Build a new menu item
     *
     */
    public MenuItemLeaveRoom() {
        super("leave", "");
    }

    /**
     * Executes the action this menu item represents
     *
     * @param argsStr        String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUIArgumentsException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUIArgumentsException {

    }
}
