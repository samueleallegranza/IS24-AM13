package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Token;

public class MenuItemReconnect extends MenuItem{

        public MenuItemReconnect() {
            super("reconnect",
                    "Reconnect to a game: ‘reconnect <your nickname before disconnecting> " +
                            "<your token color (red/blue/green/yellow) before disconnecting>’");
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
            networkHandler.reconnect(nickname, new Token(token));
        }
}
