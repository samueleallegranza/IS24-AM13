package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;

import java.util.List;

/**
 * Menu item for picking a card off the common field during a turn-based game phase.
 * It expects 2 or 3 arguments:
 * <li>
 *     <ul> Type of card to pick (resource / gold) </ul>
 *     <ul> Type of pick, referred to the chosen type of cards
 *     (draw for drawing from the deck, pick for picking one ot the 2 visible cards) </ul>
 *     <ul> If pick was chosen, which card to pick (1 for the visible card to the left, 2 for the visible card to the right) </ul>
 * </li>
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
                        "'pick <Type of card (resource/gold)> <draw/pick> [<Card to pick starting from left (1/2)>]'"
//                "Pick one of the six visible cards in the common field: pick <Number of the card to pick>"
                );
        this.state = state;
    }

    /**
     * Executes the action this menu item represents
     * @param argsStr String of parameters for the command
     * @throws InvalidTUICommandException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()<2)
            throw new InvalidTUICommandException("Parameters must be at least 2: <Type of card> <Draw or pick>");
        int cardIdx;

        if(args.get(0).equals("resource"))
            cardIdx = 0;
        else if(args.get(0).equals("gold"))
            cardIdx = 3;
        else
            throw new InvalidTUICommandException("First parameter must be the type of card ('resource' or 'gold')");

        if(args.get(1).equals("draw")) {
            if(args.size() != 2)
                throw new InvalidTUICommandException("There must be no parameters after 'draw'");
        } else if(args.get(1).equals("pick")) {
            if(args.size() != 3)
                throw new InvalidTUICommandException("There must be 1 parameter after 'pick': <Number of card to pick (1/2)");
            if(args.get(1).equals("1"))
                cardIdx += 1;
            else if(args.get(2).equals("2"))
                cardIdx += 2;
            else
                throw new InvalidTUICommandException("The third parameter must be the card to pick (1/2 starting from left)");
        }

        try {
            networkHandler.pickCard(
                    state.getPickables().get(cardIdx)
            );
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidTUICommandException("Index for card to play or where to place it are invalid");
        }

    }
}
