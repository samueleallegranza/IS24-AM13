package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

public abstract class MenuItem {

    private final String commandKey;
    private final String description;

    NetworkHandler networkHandler;

    public abstract void executeCommand(String args);
    public abstract String getCommandKey();

    public MenuItem(String commandKey, String description, NetworkHandler networkHandler) {
        this.commandKey = commandKey;
        this.description = description;
        this.networkHandler = networkHandler;
    }

    @Override
    public String toString() {
        //FIXME: format better
        return String.format("%s: %s", this.commandKey, this.description);
    }
}
