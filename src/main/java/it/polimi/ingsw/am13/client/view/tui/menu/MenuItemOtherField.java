package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIMatch;

import java.util.List;

public class MenuItemOtherField extends MenuItem {

    private final ViewTUIMatch view;

    /**
     * Build a new menu item
     * @param view View handling the printed match for TUI
     */
    public MenuItemOtherField(ViewTUIMatch view) {
        super("change_field",
                "Change the visualised field to other player field: 'change_field <Nickname of other player>");
        this.view = view;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr        String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=1)
            throw new InvalidTUICommandException("Parameters must be 1: <Nickname of the player whose field is to be visualised>");
        view.setDisplayPlayer(args.getFirst().strip());
        view.printMatch();
    }
}
