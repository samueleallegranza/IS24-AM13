package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

public class MenuItemUpdateRoomList extends MenuItem{

        public MenuItemUpdateRoomList() {
            super("update", "Update the list of available rooms: 'update'");
        }

        @Override
        public void executeCommand(String args, NetworkHandler networkHandler) {
            networkHandler.getRooms();
        }
}
