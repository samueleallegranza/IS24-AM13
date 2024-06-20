package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;

import java.util.List;

/**
 * Menu item for picking a card off the common field during a turn-based game phase.
 * It expects 2:
 * <ul>
 *     <li> Type of card to pick (res / gold) </li>
 *     <li> What card of that type to pick (draw for drawing from the deck, 1 or 2 to pick the first or the
 *     second visible card starting from left)</li>
 * </ul>
 * And then it sends the command to the server
 */
public class MenuItemPickCard extends MenuItem {

    /**
     * Reference to the current game's state
     */
    private final GameState state;

    /**
     * Build a new item for playing a card on field
     * @param state Reference to the current game's state
     */
    public MenuItemPickCard(GameState state) {
        super("pick",
                "Pick one of the six visible cards in the common field: " +
                        "'pick <Type of card (res/gold)> <draw/1/2>' (draw for first card to the left (top of deck), " +
                        "1 or 2 for first or second visible card starting from the left)"
//                "Pick one of the six visible cards in the common field: pick <Number of the card to pick>"
                );
        this.state = state;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @throws InvalidTUICommandException If the arguments passed via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=2)
            throw new InvalidTUICommandException("Parameters must be 2: <Type of card> <draw/1/2>");
        int cardIdx;

        if(args.get(0).equals("res"))
            cardIdx = 0;
        else if(args.get(0).equals("gold"))
            cardIdx = 3;
        else
            throw new InvalidTUICommandException("First parameter must be the type of card ('res' or 'gold')");

        if(args.get(1).equals("1"))
            cardIdx += 1;
        else if(args.get(1).equals("2"))
            cardIdx += 2;
        else if(!args.get(1).equals("draw"))
            throw new InvalidTUICommandException("The second parameter must be the card to pick (draw for the top of deck, " +
                    "1/2 for one of the visible cards)");

        try {
            networkHandler.pickCard(
                    state.getPickables().get(cardIdx)
            );
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidTUICommandException("Index for card to play or where to place it are invalid");
        }

    }
}
