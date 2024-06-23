package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.View;
import it.polimi.ingsw.am13.controller.LobbyException;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.CardObjectiveIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMIIF;
import it.polimi.ingsw.am13.network.rmi.LobbyRMIIF;

import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI implementation of {@link NetworkHandler}, which is used by the client to send messages to the server
 */
public class NetworkHandlerRMI implements NetworkHandler {

    /**
     * The controller that manages all the games
     */
    private final LobbyRMIIF lobby;

    /**
     * The player associated to this network handler
     */
    private PlayerLobby player;

    /**
     *  The controller of the match associated to this network handler
     */
    private GameControllerRMIIF controller;

    /**
     * The user interface
     */
    private final View view;

    /**
     * Thread sending periodically pings to server
     */
    private Thread pingThread;

    /**
     * Initializes lobby and view
     * @param lobby the controller that manages all the games
     * @param view the user interface
     */
    public NetworkHandlerRMI(LobbyRMIIF lobby, View view) {
        this.lobby = lobby;
        this.view = view;
        this.controller = null;
    }

    /**
     * @return the current valid player associated to this NetworkHandler. Null is no valid player is currently associated.
     */
    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    /**
     *
     * @return the user interface
     */
    public View getView() {
        return view;
    }

    /**
     * Sets the controller to the passed parameter
     * @param controller the controller of the match associated to this network handler
     */
    public void setController(GameControllerRMIIF controller) {
        this.controller = controller;
    }

    /**
     * Get the existing rooms
     */
    @Override
    public void getRooms() {
        try {
            List<RoomIF> rooms = lobby.getRooms();
            view.showRooms(rooms);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a room with the passed parameters
     * @param nickname of the player who is creating the room
     * @param token of the player who is creating the room
     * @param nPlayers of the room that is being created
     */
    @Override
    public void createRoom(String nickname, Token token, int nPlayers) {
        GameListenerClientRMI clientLis;
        try {
            clientLis = new GameListenerClientRMI(new PlayerLobby(nickname, token), this);
        } catch (RemoteException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        try {
            lobby.createRoom(clientLis, nPlayers);
            this.player = clientLis.getPlayer();
        } catch (LobbyException | RemoteException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Make the player join the specified room
     * @param nickname the nickname the client wants to join the room with
     * @param token chosen by the player who wants to join the room
     * @param gameId of the game that the client wants to join
     */
    @Override
    public void joinRoom(String nickname, Token token, int gameId) {
        GameListenerClientRMI clientLis;
        try {
            clientLis = new GameListenerClientRMI(new PlayerLobby(nickname, token), this);
        } catch (RemoteException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        try {
            lobby.joinRoom(gameId, clientLis);
            this.player = clientLis.getPlayer();
        } catch (LobbyException | RemoteException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Reconnect the passed player to an existing game he was previously part of
     * @param nickname of the player who wants to reconnect
     * @param token of the player who wants to reconnect
     */
    @Override
    public void reconnect(String nickname, Token token) {
        GameListenerClientRMI clientLis;
        try {
            clientLis = new GameListenerClientRMI(new PlayerLobby(nickname, token), this);
        } catch (RemoteException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        try {
            lobby.reconnectPlayer(clientLis);
            this.player = clientLis.getPlayer();
        } catch (LobbyException | RemoteException | ConnectionException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        } catch (InvalidPlayerException e) {
            //TODO pensa a questa gestione
            throw new RuntimeException(e);
        }
    }

    /**
     * Make the player leave the room he is currently part of
     */
    @Override
    public void leaveRoom() {
        try {
            lobby.leaveRoom(player);
        } catch (LobbyException | RemoteException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
        this.player = null;
    }

    /**
     * Play the starter card
     * @param side on which the starter card should be played
     */
    @Override
    public void playStarter(Side side) {
        try {
            controller.playStarter(side);
        } catch (RemoteException | InvalidPlayCardException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Choose the personal objective card
     * @param card the chosen personal objective
     */
    @Override
    public void choosePersonalObjective(CardObjectiveIF card) {
        try {
            controller.choosePersonalObjective(card);
        } catch (RemoteException | InvalidChoiceException | VariableAlreadySetException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Play a card according to the parameters
     * @param card the player wants to play
     * @param coords where the card should be played
     * @param side on which the card should be played
     */
    @Override
    public void playCard(CardPlayableIF card, Coordinates coords, Side side) {
        try {
            controller.playCard(card, side, coords);
        } catch (RemoteException | RequirementsNotMetException | InvalidPlayCardException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Pick the passed card
     * @param card the player wants to pick
     */
    @Override
    public void pickCard(CardPlayableIF card) {
        try {
            controller.pickCard(card);
        } catch (RemoteException | InvalidDrawCardException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Ping the server
     */
    private void ping() {
        try {
            controller.updatePing();
        } catch (RemoteException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the thread sending pings to server
     */
    @Override
    public void startPing() {
        pingThread = new Thread(() -> {
            while(!Thread.interrupted()) {
                ping();
                try {
                    Thread.sleep(ParametersClient.sleepTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        pingThread.start();
    }

    /**
     * Stops the thread sending pings to server
     */
    @Override
    public void stopPing() {
        pingThread.interrupt();
    }

    /**
     * Send a chat message to the receivers (either a single player, or all the players)
     *
     * @param receivers of the chat message
     * @param text      of the chat message
     */
    @Override
    public void sendChatMessage(List<PlayerLobby> receivers, String text) {
        try {
            controller.transmitChatMessage(receivers,text);
        } catch (RemoteException e) {
            view.showException(e);
        }
    }

}
