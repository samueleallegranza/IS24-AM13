package it.polimi.ingsw.am13.client.gamestate;

import it.polimi.ingsw.am13.model.GameModelIF;
import it.polimi.ingsw.am13.model.card.CardPlayableIF;
import it.polimi.ingsw.am13.model.card.CardStarterIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMI;
import it.polimi.ingsw.am13.network.rmi.GameControllerRMIIF;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class GameStateHandler {

    private final GameState state;

    public GameStateHandler(GameModelIF model) {
        state = new GameState(model);
    }

    public GameStateHandler(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    //TODO pensa a quali update servono e quali no

    public void updatePlayerJoinedRoom(PlayerLobby player) throws RemoteException {

    }

    public void updatePlayerLeftRoom(PlayerLobby player) throws RemoteException {

    }

    public void updateGameBegins(GameControllerRMIIF controllerRMI) throws RemoteException {

    }

    public void updateStartGame(GameModelIF model) throws RemoteException {

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
