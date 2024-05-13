package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.HashMap;
import java.util.Map;

public class ViewTUIRoom implements ViewTuiMenuDisplayer {

    private final Map<String, MenuItem> menu;

    public ViewTUIRoom() {
        menu = new HashMap<>();
        //TODO: popola menu
    }

    public void printJoinedRoom() {
        System.out.println("You have joined the room");
    }

    public void printPlayerJoinedRoom(PlayerLobby player) {
        System.out.println("Player " + player + " joined the room");
    }

    public void printPlayerLeftRoom(PlayerLobby player) {
        System.out.println("Player " + player + " left the room");
    }

    @Override
    public Map<String, MenuItem> getMenu() {
        return menu;
    }
}
