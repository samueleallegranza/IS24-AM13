package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.controller.RoomIF;
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

    public abstract void showStartupScreen(boolean isSocket, String ip, int port);
    public abstract void showRooms(List<RoomIF> rooms);
    public abstract void showPlayerJoinedRoom(PlayerLobby player);

    public abstract void showException(Exception e);
    public abstract void setGameId(int gameId);
}
