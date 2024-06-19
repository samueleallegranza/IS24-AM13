package it.polimi.ingsw.am13.client.view.tui.menu;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.ViewTUI;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu item for entering a chat room
 * It expects 1 argument
 * <ul>
 *     <li> Player's nickname associated to the chat room, or 'all' for the global chatroom with all the players </li>
 * </ul>
 * And then it sends the command to the server
 */
public class MenuItemEnterChat extends MenuItem {

    /**
     * View handling the printed match for TUI
     */
    private final ViewTUI view;

    /**
     * Build a new menu item
     */
    public MenuItemEnterChat(ViewTUI view) {
        super( "chat",
                "enter the chat: 'chat <player's nickname / all>'");
        this.view = view;
    }

    @Override
    public void executeCommand(String argsStr, NetworkHandler networkHandler) throws InvalidTUICommandException {
        List<String> args = List.of(argsStr.split("\\s+"));
        if(args.size()!=1)
            throw new InvalidTUICommandException("There must be 1 parameter: <Player's nickname associated to the chat room, or 'all' for the global chatroom with all the players> ");

        String playerName = args.getFirst().strip();
        List<String> receivers = new ArrayList<>();
        if(playerName.equals("all"))
            receivers.addAll(view.getGameState().getPlayers().stream().filter(p -> !p.equals(view.getThisPlayer()))
                    .map(PlayerLobby::getNickname).toList());
        else
            receivers.add(playerName);

        try {
            view.enterChatRoom(receivers);
        } catch (InvalidParameterException e) {
            throw new InvalidTUICommandException("You enter a wrong receiver, it must be another player or 'all' for " +
                    "the chat with all the players");

        }
    }
}
