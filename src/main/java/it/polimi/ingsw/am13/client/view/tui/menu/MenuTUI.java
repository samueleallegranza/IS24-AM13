package it.polimi.ingsw.am13.client.view.tui.menu;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu storing all possible menu items corresponding to the is possible executable action in the current state of the game.
 */
public class MenuTUI {
    /**
     * Map of menu items, with the command key as key
     */
    private final Map<String, MenuItem> menu;
    /**
     * Request to be printed before the menu
     */
    private final String request;

    /**
     * Builds a new given the list of {@link MenuItem} which the menu is composed of.
     * @param menuItems list of MenuItem instances
     */
    public MenuTUI(String request, MenuItem ... menuItems) {
        Map<String, MenuItem> newMenu = new HashMap<>();
        for(MenuItem item : menuItems)
            newMenu.put(item.getCommandKey(), item);
        this.menu = newMenu;
        this.request = request;
    }

    /**
     * Prints (text) the menu
     */
    public void printMenu() {
        if(request != null && !request.isEmpty())
            System.out.println(this.request);

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
