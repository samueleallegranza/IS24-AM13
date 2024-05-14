package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.view.tui.menu.*;
import it.polimi.ingsw.am13.controller.RoomIF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTUILobby {

    private HashMap<String, MenuItem> menu;

    public ViewTUILobby() {
        menu = new HashMap<>();
    }

    public Map<String, MenuItem> getMenu() {
        return menu;
    }

    public void printRooms(List<RoomIF> rooms) {
        System.out.println("Rooms:");
        for (RoomIF r : rooms) {
            System.out.println(r);
        }

        this.menu = MenuItem.menuBuilder(List.of(
                new MenuItemUpdateRoomList(),
                new MenuItemCreateRoom(),
                new MenuItemJoinRoom()
                )
        );
        MenuItem.printMenu(this.menu);
    }

    public void printLeftRoom() {
        System.out.println("You have left the room");
    }
}
