package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Token;

/**
 * Menu item for joining an existing room.
 * If parses the input as
 * <ul>
 *     <li> Nickname (1 word) </li>
 *     <li> Color of token (red / blue / green / yellow) </li>
 *     <li> Game id of the room to join </li>
 * </ul>
 * It then sends to the server the command for joining the specified room
 */
public class MenuItemJoinRoom extends MenuItem {

        public MenuItemJoinRoom() {
            super("join",
                    "Join a room: ‘join <your nickname> <your token color (red/blue/green/yellow)> <game id of the room to join>’");
        }

        @Override
        public void executeCommand(String args, NetworkHandler networkHandler) throws InvalidTUICommandException {
            if (args == null || args.isEmpty()) {
                throw new InvalidTUICommandException("Invalid arguments. Please specify the room ID.");
            }
            String[] arg = args.split(" ");
            if(arg.length!=3)
                throw new InvalidTUICommandException("Arguments must be 3: <your nickname> <your token color (red/blue/green/yellow)> <game id of the room to join>");
            String nickname = arg[0];
            ColorToken token;
            try {
                token = ColorToken.valueOf(arg[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTUICommandException("Invalid token color. Please choose one of the following: red, blue, green, yellow");
            }
            int roomID = Integer.parseInt(arg[2]) - 1;
            if (roomID < 0) {
                throw new InvalidTUICommandException("Invalid room ID. Please choose a positive number");
            }
            networkHandler.joinRoom(nickname, new Token(token), roomID);
        }
}
