package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.List;

/**
 * Menu item for playing a card on field during a turn-based game phase.
 * It expects 3 arguments (card to play, where to place it and on which side) and sends the command to the server
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
     * @throws InvalidTUIArgumentsException If the arguments passad via command line are wrong, or anyway different from what expected
     */
    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUIArgumentsException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=3)
            throw new InvalidTUIArgumentsException("Parameters must be 3: <Number of card to play>, <Number of spot where" +
                    "to place it>, <Side on which to play it (F/B)");
        int cardIdx;
        int coordIdx;
        Side side;

        try {
            cardIdx = Integer.parseInt(args.getFirst());
        } catch (NumberFormatException e) {
            throw new InvalidTUIArgumentsException("First parameter must be an integer indicating the card to play");
        }
        try {
            coordIdx = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            throw new InvalidTUIArgumentsException("Second parameter must be an integer indicating the spot where to place the card");
        }
        if(args.get(2).equals("F"))
            side = Side.SIDEFRONT;
        else if(args.get(2).equals("B"))
            side = Side.SIDEBACK;
        else
            throw new InvalidTUIArgumentsException("Third parameter must be the side on which to play the card (F for front, B for back");

        //TODO forse è da sistemare la numerazione (non so se parte da 1 o da 0)
        PlayerLobby player = networkHandler.getPlayer();
        try {
            networkHandler.playCard(
                    state.getPlayerState(player).getHandPlayable().get(cardIdx),
                    state.getPlayerState(player).getField().getAvailableCoords().get(coordIdx),
                    side
            );
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidTUIArgumentsException("Index for card to play or where to place it are invalid");
        }
    }

}
