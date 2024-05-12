package it.polimi.ingsw.am13.client.view.tui;

import java.util.Map;

public interface ViewTUI {
    Map<String, MenuItem> getMenu();

    void printView();
}
