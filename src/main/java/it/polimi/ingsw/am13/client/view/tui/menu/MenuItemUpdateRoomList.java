package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

/**
 * Menu item for updating the listed rooms.
 * It simply sends the command for listing rooms to the server
 */
public class MenuItemUpdateRoomList extends MenuItem{

        /**
         * Build a new item for updating the list of rooms
         */
        public MenuItemUpdateRoomList() {
            super("update", "Update the list of available rooms");
        }

        /**
         * Executes the action this menu item represents
         * @param args        String of parameters for the command
         * @param networkHandler Handler of the network thanks to which the item sends the command to the server
         * @throws InvalidTUICommandException If the arguments passed via command line are wrong, or anyway different from what expected
         */
        @Override
        public void executeCommand(String args, NetworkHandler networkHandler) throws InvalidTUICommandException {
            if (!args.isEmpty())
                throw new InvalidTUICommandException("This command does not require any other argument");
            networkHandler.getRooms();
        }
}
