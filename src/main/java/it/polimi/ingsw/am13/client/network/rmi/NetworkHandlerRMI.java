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
import it.polimi.ingsw.am13.network.rmi.GameControllerRMI;
import it.polimi.ingsw.am13.network.rmi.LobbyRMI;

import java.rmi.RemoteException;
import java.util.List;

public class NetworkHandlerRMI implements NetworkHandler {

    private final LobbyRMI lobby;

    private PlayerLobby player;

    private GameControllerRMI controller;

    private final View view;

    public NetworkHandlerRMI(LobbyRMI lobby, View view) {
        this.lobby = lobby;
        this.view = view;
        this.controller = null;
    }

    public PlayerLobby getPlayer() {
        return player;
    }

    public View getView() {
        return view;
    }

    public void setController(GameControllerRMI controller) {
        this.controller = controller;
    }

    //TODO: come gestire le remote exception ???
    // Potrei gestirle come le altre eccezioni e notificare semplicementente alla view che qualcosa è andato storto

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

}
