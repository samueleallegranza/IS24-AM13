package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Token;

public class MenuItemJoinRoom extends MenuItem{

        public MenuItemJoinRoom() {
            super("join", "Join a room: ‘join <your nickname> <your token color (red/blue/green/yellow)> <game id of the game to join> ’");
        }

        @Override
        public void executeCommand(String args, NetworkHandler networkHandler) throws InvalidTUIArgumentsException {
            if (args == null || args.isEmpty()) {
                throw new InvalidTUIArgumentsException("Invalid arguments. Please specify the room ID.");
            }
            String[] arg = args.split(" ");
            String nickname = arg[0];
            ColorToken token;
            try {
                token = ColorToken.valueOf(arg[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTUIArgumentsException("Invalid token color. Please choose one of the following: red, blue, green, yellow");
            }
            int roomID = Integer.parseInt(arg[2]);
            if (roomID < 0) {
                throw new InvalidTUIArgumentsException("Invalid room ID. Please choose a positive number");
            }
            networkHandler.joinRoom(nickname, new Token(token), roomID);
        }
}
