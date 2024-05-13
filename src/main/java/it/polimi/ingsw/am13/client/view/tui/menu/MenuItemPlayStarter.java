package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.card.Side;

import java.util.List;

/**
 * Menu item for playing the starting card in INIT game phase.
 * It expects 1 argument (side on which to play the starter card) and sends the command to the server
 */
public class MenuItemPlayStarter extends MenuItem {

    /**
     * Build a new item for playing the starter card
     */
    public MenuItemPlayStarter() {
        super("play_starter",
                "Choose the side on which to play the starter card: 'play_starter <Side (front/back)>'"
                );
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUIArgumentsException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUIArgumentsException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=1)
            throw new InvalidTUIArgumentsException("There must be 1 parameter: <Side on which to play the started card (front/back>");

        Side side;
        if(args.getFirst().equals("front"))
            side = Side.SIDEFRONT;
        else if(args.getFirst().equals("back"))
            side = Side.SIDEBACK;
        else
            throw new InvalidTUIArgumentsException("The parameter must be an integer representing the objective card to choose starting from left (1/2)");

        networkHandler.playStarter(side);
    }
}
