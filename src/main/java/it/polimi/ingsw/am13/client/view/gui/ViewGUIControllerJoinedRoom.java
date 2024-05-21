package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class ViewGUIControllerJoinedRoom extends ViewGUIController {
    public TableColumn<PlayerLobby,String> joinedRoomPlayersColumn;
    public TableView<PlayerLobby> joinedRoomTable;
    private int gameId;

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void showPlayerJoinedRoom(PlayerLobby player){
        //joinedRoomPlayersColumn.setCellValueFactory(new PropertyValueFactory<PlayerLobby,String>("Nickname"));
        ObservableList<PlayerLobby> observablePlayer=FXCollections.observableList(new ArrayList<>(List.of(player)));
        joinedRoomTable.getItems().add(observablePlayer.get(0));
    }

    @Override
    public void showException(Exception e) {

    }

    public void onLeaveRoomClick(ActionEvent actionEvent) {
        networkHandler.leaveRoom();
    }

    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {
        throw new RuntimeException();
    }

    @Override
    public void showRooms(List<RoomIF> rooms) {
        joinedRoomTable.getItems().clear();
        joinedRoomPlayersColumn.setCellValueFactory(new PropertyValueFactory<PlayerLobby,String>("Nickname"));
        if(rooms.size()>=gameId) {
            ObservableList<PlayerLobby> observablePlayers = FXCollections.observableList(rooms.get(gameId).getPlayers());
            for (PlayerLobby playerLobby : observablePlayers)
                joinedRoomTable.getItems().add(playerLobby);
        }
    }
}
