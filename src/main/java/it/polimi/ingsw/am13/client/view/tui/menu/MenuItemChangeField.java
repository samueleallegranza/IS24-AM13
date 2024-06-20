package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.ViewTUIMatch;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Menu item to change the displayed field to that of another player
 */
public class MenuItemChangeField extends MenuItem {

    /**
     * View handling the printed match for TUI
     */
    private final ViewTUIMatch view;

    /**
     * Build a new menu item
     * @param view View handling the printed match for TUI
     */
    public MenuItemChangeField(ViewTUIMatch view) {
        super("change_field",
                "Change the visualised field to other player field: 'change_field <Nickname of other player>");
        this.view = view;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr        String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If the arguments passed via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=1)
            throw new InvalidTUICommandException("Parameters must be 1: <Nickname of the player whose field is to be visualised>");
        try {
            view.setDisplayPlayer(args.getFirst().strip());
        } catch (InvalidParameterException e) {
            throw new InvalidTUICommandException("Parameter must be the name of a valid player");
        }
        view.printMatch();
    }
}
