package it.polimi.ingsw.am13.client.network.rmi;

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

    private final LobbyRMIIF lobby;

    private PlayerLobby player;

    private GameControllerRMIIF controller;

    private final View view;

    public NetworkHandlerRMI(LobbyRMIIF lobby, View view) {
        this.lobby = lobby;
        this.view = view;
        this.controller = null;
    }

    @Override
    public PlayerLobby getPlayer() {
        return player;
    }

    public View getView() {
        return view;
    }

    public void setController(GameControllerRMIIF controller) {
        this.controller = controller;
    }

    @Override
    public void getRooms() {
        try {
            List<RoomIF> rooms = lobby.getRooms();
            view.showRooms(rooms);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

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

    @Override
    public void playStarter(Side side) {
        try {
            controller.playStarter(side);
        } catch (RemoteException | InvalidPlayCardException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void choosePersonalObjective(CardObjectiveIF card) {
        try {
            controller.choosePersonalObjective(card);
        } catch (RemoteException | InvalidChoiceException | VariableAlreadySetException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void playCard(CardPlayableIF card, Coordinates coords, Side side) {
        try {
            controller.playCard(card, side, coords);
        } catch (RemoteException | RequirementsNotMetException | InvalidPlayCardException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void pickCard(CardPlayableIF card) {
        try {
            controller.pickCard(card);
        } catch (RemoteException | InvalidDrawCardException | GameStatusException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void ping() {
        try {
            controller.updatePing();
        } catch (RemoteException e) {
            view.showException(e);
//            throw new RuntimeException(e);
        }
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
