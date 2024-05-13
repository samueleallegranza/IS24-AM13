package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItemCreateRoom;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItemJoinRoom;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItemReconnect;
import it.polimi.ingsw.am13.controller.RoomIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTUILobby {

    private final Map<String, MenuItem> menu;

    public ViewTUILobby() {
        List<MenuItem> items = List.of(
                new MenuItemCreateRoom(),
                new MenuItemJoinRoom(),
                new MenuItemReconnect()
        );
        menu = new HashMap<>();
        for(MenuItem item : items) {
            menu.put(item.getCommandKey(), item);
        }
    }

    public Map<String, MenuItem> getMenu() {
        return menu;
    }

    public void printRooms(List<RoomIF> rooms) {
        for (RoomIF r : rooms) {
            System.out.println(r);
        }
    }

    public void printLeftRoom() {
        System.out.println("You have left the room");
    }
}
