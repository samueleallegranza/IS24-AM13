package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIMatch;

/**
 * Menu item to disable the print of the legend in field view
 */
public class MenuItemDisableLegend extends MenuItem {

    /**
     * View handling the printed match for TUI
     */
    private final ViewTUIMatch view;

    /**
     * Creates a new menu item to disable the print of the legend
     * @param view View handling the printed match for TUI
     */
    public MenuItemDisableLegend(ViewTUIMatch view) {
        super("disable_legend",
                "Stops showing the legend of the symbols for cards");
        this.view = view;
    }

    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        if(!argsStr.isEmpty())
            throw new InvalidTUICommandException("Thi command does not require any other argument");
        view.setShowLegend(false);
        view.printMatch();
    }
}
