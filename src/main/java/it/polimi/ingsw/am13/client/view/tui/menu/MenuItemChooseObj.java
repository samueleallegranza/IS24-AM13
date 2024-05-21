package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;

import java.util.List;

/**
 * Menu item for choosing the personal objective card during INIT game phase
 * It expects 1 argument (which card to choose) and sends the command to the server
 */
public class MenuItemChooseObj extends MenuItem {

    /**
     * Reference to the current game's state
     */
    private final GameState state;

    /**
     * Build a new item for choosing the personal objective card
     * @param state Reference to the current game's state
     */
    public MenuItemChooseObj(GameState state) {
        super("choose_obj",
                "Choose your personal objective card: 'choose_obj <Card to choose starting from left (1/2)>'"
                );
        this.state = state;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @param networkHandler Handler of the network thanks to which the item sends the command to the server
     * @throws InvalidTUICommandException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=1)
            throw new InvalidTUICommandException("There must be 1 parameter: <Card to choose starting from left (1/2)>");

        int cardIdx;
        if(args.getFirst().equals("1"))
            cardIdx = 0;
        else if(args.getFirst().equals("2"))
            cardIdx = 1;
        else
            throw new InvalidTUICommandException("The parameter must be an integer representing the objective card to choose starting from left (1/2)");

        networkHandler.choosePersonalObjective(state.getPlayerState(networkHandler.getPlayer()).getPossibleHandObjectives().get(cardIdx));
    }
}
