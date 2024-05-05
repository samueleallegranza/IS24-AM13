package it.polimi.ingsw.am13.client.network.rmi;

import it.polimi.ingsw.am13.controller.GameController;
import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMI;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMIIF;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

//TODO: scrivi documentazione
public class GameListenerClientRMI extends UnicastRemoteObject implements Remote {
    
    private final PlayerLobby player;
    
    public GameListenerClientRMI(PlayerLobby player) throws RemoteException {
        super();
        this.player = player;
    }

    public PlayerLobby getPlayer() throws RemoteException {
        return player;
    }



    public void updatePlayerJoinedRoom(PlayerLobby player) throws RemoteException {

    }

    public void updatePlayerLeftRoom(PlayerLobby player) throws RemoteException {

    }


    public void updateStartGame(GameModelIF model, GameController controller) throws RemoteException {

    }

    public void updatePlayedStarter(PlayerLobby player, CardStarterIF cardStarter) throws RemoteException {

    }

    public void updateChosenPersonalObjective(PlayerLobby player) throws RemoteException {

    }

    public void updateNextTurn(PlayerLobby player) throws RemoteException {

    }

    public void updatePlayedCard(PlayerLobby player, CardPlayableIF cardPlayable, Side side, Coordinates coord, int points, List<Coordinates> availableCoords) throws RemoteException {

    }

    public void updatePickedCard(PlayerLobby player, List<? extends CardPlayableIF> updatedVisibleCards) throws RemoteException {

    }

    public void updatePoints(Map<PlayerLobby, Integer> pointsMap) throws RemoteException {

    }

    public void updateWinner(PlayerLobby winner) throws RemoteException {

    }

    public void updateEndGame() throws RemoteException {

    }

    public void updatePlayerDisconnected(PlayerLobby player) throws RemoteException {

    }

    public void updatePlayerReconnected(PlayerLobby player) throws RemoteException {

    }

    public void updateFinalPhase() throws RemoteException {

    }

    public void updateInGame() throws RemoteException {

    }

    public void updateGameModel(GameModelIF model, GameControllerRMIIF controllerRMI) throws RemoteException {

    }

    public void updateGameController(GameControllerRMI gameControllerRMI) throws RemoteException {

    }
}
