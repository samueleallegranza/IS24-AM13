package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

/**
 * Menu item for updating the listed rooms.
 * It simply sends the command for listing rooms to the server
 */
public class MenuItemUpdateRoomList extends MenuItem{

        public MenuItemUpdateRoomList() {
            super("update", "Update the clist of available rooms: 'update'");
        }

        @Override
        public void executeCommand(String args, NetworkHandler networkHandler) {
            networkHandler.getRooms();
        }
}
