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

    /**
     * The room this client belongs to
     */
    private RoomIF thisRoom;

    /**
     * Method that sets the player
     * @param player the player associated to this {@link ViewGUIController}
     */
    @Override
    public void setThisPlayer(PlayerLobby player) {
    }

    /**
     * Method that sets the gameState
     * @param state the representation of the state of the game
     */
    @Override
    public void setGameState(GameState state) {
    }

    /**
     *
     * @return the title of the scene
     */
    @Override
    public String getSceneTitle() {
        return "Codex";
    }

    /**
     * It is never called because the only action that the player can perform in this scene
     * is leaving the room (which is always valid)
     * @param e Exception to be shown
     */
    @Override
    public void showException(Exception e) {
    }

    /**
     * This method cannot be called
     * @param player Player who disconnected
     */
    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
    }

    /**
     * It shows that a player reconnected to an ongoing game by calling the corresponding method on {@link ViewGUIController}.
     * @param player Player who reconnected
     */
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
    }

    /**
     * Update the label of this room
     */
    private void updateRoomLabel() {
        Platform.runLater(() -> roomLabel.setText(String.format("Room %05d - %d/%d", thisRoom.getGameId(),
                thisRoom.getPlayers().size(), thisRoom.getnPlayersTarget())));
    }

    /**
     * Sets this room and then calls {@link #updateRoomLabel()}
     * @param room the room this client belongs to
     */
    public void setRoom(RoomIF room) {
        this.thisRoom = room;
        updateRoomLabel();
    }

    /**
     * Shows that a player joined the room by adding it to the {@link #joinedRoomTable} and then calling
     * {@link #updateRoomLabel()}
     * @param player who joined the room
     */
    public void showPlayerJoinedRoom(PlayerLobby player){
        //joinedRoomPlayersColumn.setCellValueFactory(new PropertyValueFactory<PlayerLobby,String>("Nickname"));
        ObservableList<PlayerLobby> observablePlayer=FXCollections.observableList(new ArrayList<>(List.of(player)));
        joinedRoomTable.getItems().add(observablePlayer.getFirst());
        updateRoomLabel();
    }

    /**
     * Initialises the {@link #joinedRoomTable}
     * @param rooms list of the existing rooms
     */
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

    /**
     * Leave the room when the player clicks on the corresponding button
     * @param actionEvent a click performed by the client
     */
    @FXML
    public void onLeaveRoomClick(ActionEvent actionEvent) {
        networkHandler.leaveRoom();
    }
}
