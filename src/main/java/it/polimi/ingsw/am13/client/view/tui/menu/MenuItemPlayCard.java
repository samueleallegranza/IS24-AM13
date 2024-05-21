package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Menu item for playing a card on field during a turn-based game phase.
 * It expects 3 arguments:
 * <li>
 *     <ul> Number of card to play (index of the card in the list of hand cards in game's state </ul>
 *     <ul> Number of spot where to place the card in field (index of the coordinates in the list of
 *     available coordinates for that field in game's state </ul>
 *     <ul> Side on which to play the card (front / back) </ul>
 * </li>
 * And then it sends the command to the server
 */
public class MenuItemPlayCard extends MenuItem {

    /**
     * Reference to the current game's state
     */
    private final GameState state;

    /**
     * Build a new item for playing a card on field
     * @param state Reference to the current game's state
     */
    public MenuItemPlayCard(GameState state) {
        super("play",
                "Choose the card to play: 'play <Number of card> <Number of spot in field> <Side (front/back)>'"
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
        if(args.size()!=3)
            throw new InvalidTUICommandException("Parameters must be 3: <Number of card to play>, <Number of spot where" +
                    "to place it>, <Side on which to play it (front/back)>");
        int cardIdx;
        int coordIdx;
        Side side;

        try {
            cardIdx = Integer.parseInt(args.getFirst()) - 1;
        } catch (NumberFormatException e) {
            throw new InvalidTUICommandException("First parameter must be an integer indicating the card to play");
        }
        try {
            coordIdx = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            throw new InvalidTUICommandException("Second parameter must be an integer indicating the spot where to place the card");
        }
        if(args.get(2).equals("front"))
            side = Side.SIDEFRONT;
        else if(args.get(2).equals("back"))
            side = Side.SIDEBACK;
        else
            throw new InvalidTUICommandException("Third parameter must be the side on which to play the card (F for front, B for back");

        PlayerLobby player = networkHandler.getPlayer();
        try {
            networkHandler.playCard(
                    state.getPlayerState(player).getHandPlayable().get(cardIdx),
                    state.getPlayerState(player).getField().getAvailableCoords().get(coordIdx),
                    side
            );
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidTUICommandException("Index for card to play or where to place it are invalid");
        }
    }

}
