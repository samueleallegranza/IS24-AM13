package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIMatch;

/**
 * Menu item to enable the print of the legend in field view
 */
public class MenuItemEnableLegend extends MenuItem {

    /**
     * View handling the printed match for TUI
     */
    private final ViewTUIMatch view;

    /**
     * Creates a new menu item to enable the print of the legend
     * @param view View handling the printed match for TUI
     */
    public MenuItemEnableLegend(ViewTUIMatch view) {
        super("show_legend",
                "Shows the legend of the symbols for cards");
        this.view = view;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If If there are argument passed via command line
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        if(!argsStr.isEmpty())
            throw new InvalidTUICommandException("Thi command does not require any other argument");
        view.setShowLegend(true);
        view.printMatch();
    }
}
