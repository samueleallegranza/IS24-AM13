package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.client.gamestate.GameStateHandler;
import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

//TODO: scrivi documentazione
public class GameListenerClientRMI extends UnicastRemoteObject implements Remote {
    
    private final PlayerLobby player;

    private final NetworkHandlerRMI networkHandler;

    private GameStateHandler stateHandler;
    
    public GameListenerClientRMI(PlayerLobby player, NetworkHandlerRMI networkHandler) throws RemoteException {
        super();
        this.player = player;
        this.networkHandler = networkHandler;
        stateHandler = null;
    }


    //TODO: implementazione da finire con le chiamate ai metodi di view


    public PlayerLobby getPlayer() throws RemoteException {
        return player;
    }

    public void updatePlayerJoinedRoom(PlayerLobby player) throws RemoteException {
        //TODO devo solo notificare la view
    }

    public void updatePlayerLeftRoom(PlayerLobby player) throws RemoteException {
        //TODO devo solo notificate la view
    }

    public void updateStartGame(GameModelIF model, GameController controller) throws RemoteException, InvalidPlayerException {
        GameControllerRMI controllerRMI = new GameControllerRMI(controller, player);
        networkHandler.setController(controllerRMI);
        stateHandler = new GameStateHandler(model);
    }

    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter) throws RemoteException {
        stateHandler.updatePlayedStarter(player, cardStarter);
    }

    public void updateChosenPersonalObjective(PlayerLobby player) throws RemoteException {
        stateHandler.updateChosenPersonalObjective(player);
    }

    public void updateNextTurn(PlayerLobby player) throws RemoteException {
        stateHandler.updateNextTurn(player);
    }

    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side,
                                 Coordinates coord, int points, List<Coordinates> availableCoords) throws RemoteException {
        stateHandler.updatePlayedCard(player, cardPlayable, side, coord, points, availableCoords);
    }

    public void updatePickedCard(PlayerLobby player, List<CardPlayableIF> updatedVisibleCards) throws RemoteException {
        stateHandler.updatePickedCard(player, updatedVisibleCards);
    }

    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) throws RemoteException {
        stateHandler.updatePoints(pointsMap);
    }

    public void updateWinner(PlayerLobby winner) throws RemoteException {
        stateHandler.updateWinner(winner);
    }

    public void updateEndGame() throws RemoteException {
        stateHandler.updateEndGame();
    }

    public void updatePlayerDisconnected(PlayerLobby player) throws RemoteException {
        stateHandler.updatePlayerDisconnected(player);
    }

    public void updatePlayerReconnected(PlayerLobby player) throws RemoteException {
        stateHandler.updatePlayerReconnected(player);
    }

    public void updateFinalPhase() throws RemoteException {
        stateHandler.updateFinalPhase();
    }

    public void updateInGame() throws RemoteException {
        stateHandler.updateInGame();
    }

    public void updateGameModel(GameModelIF model, GameController controller) throws RemoteException, InvalidPlayerException {
        GameControllerRMI controllerRMI = new GameControllerRMI(controller, player);
        networkHandler.setController(controllerRMI);
        stateHandler = new GameStateHandler(model);
    }
}
