package it.polimi.ingsw.am13.client.view.tui.menu;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu storing all possible menu items the is possible to execute
 */
public class MenuTUI {

    private final Map<String, MenuItem> menu;

    /**
     * Builds a new given the list of {@link MenuItem} which the menu is composed of.
     * @param menuItems list of MenuItem instances
     */
    public MenuTUI(MenuItem ... menuItems) {
        Map<String, MenuItem> newMenu = new HashMap<>();
        for(MenuItem item : menuItems)
            newMenu.put(item.getCommandKey(), item);
        this.menu = newMenu;
    }

    /**
     * Prints (text) the menu
     */
    public void printMenu() {
        System.out.println("Menu:");
        for(String k: menu.keySet()) System.out.printf("\t%s\n", menu.get(k).toString());
        System.out.print("> ");
    }

    /**
     * @param commandKey Command key of the menu item
     * @return The menu item corresponding to the given command key, or null if there is no item with that command key
     */
    public MenuItem getItem(String commandKey) {
        return menu.get(commandKey);
    }
}
