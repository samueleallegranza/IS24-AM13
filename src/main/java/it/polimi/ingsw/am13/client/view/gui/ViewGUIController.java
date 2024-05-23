package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.stage.Stage;

import java.util.List;

public abstract class ViewGUIController {
    protected Stage stage;
    protected NetworkHandler networkHandler;
    public void setStage(Stage stage){
        this.stage = stage;
    }
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }
    public abstract void setPlayer(PlayerLobby player);
    public abstract void setGameState(GameState gameState);
    public abstract void showStartupScreen(boolean isSocket, String ip, int port);
    public abstract void showRooms(List<RoomIF> rooms);
    public abstract void showPlayerJoinedRoom(PlayerLobby player);

    public abstract void showStartGame(GameState state);

    public abstract void showException(Exception e);
    public abstract void setGameId(int gameId);

    public abstract void showPlayedStarter(PlayerLobby player);

    public abstract void showChosenPersonalObjective(PlayerLobby player);

    public abstract void showInGame();
    public abstract void showPlayedCard(PlayerLobby player, Coordinates coord);

    public abstract void showPickedCard(PlayerLobby player);

    public abstract void showNextTurn();

}
