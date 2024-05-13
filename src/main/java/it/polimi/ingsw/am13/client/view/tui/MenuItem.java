package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.network.NetworkHandler;

public abstract class MenuItem {

    private NetworkHandler networkHandler;

    public abstract void executeCommand();
    public abstract String getCommandKey();
}
