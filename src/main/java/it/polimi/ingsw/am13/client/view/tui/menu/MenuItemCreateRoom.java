package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Token;

/**
 * Menu item for creating a room.
 * It parses the parameters as:
 * <li>
 *     <ul> Nickname (1 word) </ul>
 *     <ul> Color of token (red / blue / green / yellow) </ul>
 *     <ul> Number of players to make the game start </ul>
 * </li>
 * It then sends to the server the command for creating a new room
 */
public class MenuItemCreateRoom extends MenuItem {

    public MenuItemCreateRoom() {
        super("create",
                "Create a room: 'create <your nickname> <your token color (red/blue/green/yellow)> <number of players (2/3/4)>'");
    }
    @Override
    public void executeCommand(String args, NetworkHandler networkHandler) throws InvalidTUICommandException {
        if (args == null || args.isEmpty()) {
            throw new InvalidTUICommandException("Invalid arguments. Please specify the room ID.");
        }
        String[] arg = args.split(" ");
        String nickname = arg[0];
        ColorToken token;
        try {
            token = ColorToken.valueOf(arg[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTUICommandException("Invalid token color. Please choose one of the following: red, blue, green, yellow");
        }
        int numPlayers = Integer.parseInt(arg[2]);
        if (numPlayers < 2 || numPlayers > 4) {
            throw new InvalidTUICommandException("Invalid number of players. Please choose a number between 2 and 4");
        }
        networkHandler.createRoom(nickname, new Token(token), numPlayers);
    }
}
