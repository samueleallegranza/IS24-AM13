package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;

import java.util.Map;

//todo rimuovi se non è usata
public interface ViewTuiMenuDisplayer {

    Map<String, MenuItem> getMenu();

}