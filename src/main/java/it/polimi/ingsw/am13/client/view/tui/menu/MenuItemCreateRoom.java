package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Token;

public class MenuItemCreateRoom extends MenuItem {

    public MenuItemCreateRoom() {
        super("create",
                "Create a room: 'create <your nickname> <your token color (red/blue/green/yellow)> <number of players (2/3/4)>'");
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
        int numPlayers = Integer.parseInt(arg[2]);
        if (numPlayers < 2 || numPlayers > 4) {
            throw new InvalidTUIArgumentsException("Invalid number of players. Please choose a number between 2 and 4");
        }
        networkHandler.createRoom(nickname, new Token(token), numPlayers);
    }
}
