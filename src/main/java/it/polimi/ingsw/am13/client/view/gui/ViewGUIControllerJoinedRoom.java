package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller of 'JoinedRoom' scene, where player can wait for the room to get full or can leave the room
 */
public class ViewGUIControllerJoinedRoom extends ViewGUIController {

    @FXML
    public TableColumn<PlayerLobby,String> joinedRoomPlayersColumn;
    @FXML
    public TableView<PlayerLobby> joinedRoomTable;
    @FXML
    private Label roomLabel;

    private RoomIF thisRoom;


    @Override
    public void setThisPlayer(PlayerLobby player) {
    }
    @Override
    public void setGameState(GameState state) {
    }

    @Override
    public String getSceneTitle() {
        return "Codex";
    }

    @Override
    public void showException(Exception e) {
        //TODO: forse da implementare
    }
    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
    }
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
    }


    private void updateRoomLabel() {
        Platform.runLater(() -> roomLabel.setText(String.format("Room %05d - %d/%d", thisRoom.getGameId(),
                thisRoom.getPlayers().size(), thisRoom.getnPlayersTarget())));
    }

    public void setRoom(RoomIF room) {
        this.thisRoom = room;
        updateRoomLabel();
    }

    public void showPlayerJoinedRoom(PlayerLobby player){
        //joinedRoomPlayersColumn.setCellValueFactory(new PropertyValueFactory<PlayerLobby,String>("Nickname"));
        ObservableList<PlayerLobby> observablePlayer=FXCollections.observableList(new ArrayList<>(List.of(player)));
        joinedRoomTable.getItems().add(observablePlayer.getFirst());
        updateRoomLabel();
    }

    public void showRooms(List<RoomIF> rooms) {
        joinedRoomTable.getItems().clear();
        joinedRoomPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("Nickname"));
        RoomIF myRoom = null;
        for(RoomIF room : rooms)
            if(room.getGameId() == thisRoom.getGameId())
                myRoom = room;
        if(myRoom!=null) {
            ObservableList<PlayerLobby> observablePlayers = FXCollections.observableList(myRoom.getPlayers());
            for (PlayerLobby playerLobby : observablePlayers)
                joinedRoomTable.getItems().add(playerLobby);
        }
    }

    @FXML
    public void onLeaveRoomClick(ActionEvent actionEvent) {
        networkHandler.leaveRoom();
    }
}
