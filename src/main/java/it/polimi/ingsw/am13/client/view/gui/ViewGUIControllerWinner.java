package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.gamestate.PlayerState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.util.List;

public class ViewGUIControllerWinner extends ViewGUIController{
    public Label winnerText;
    public TableView<PlayerLobby> pointsTable;
    public TableColumn<PlayerLobby,String> playerColumn;
    public TableColumn<PlayerLobby,String> pointsColumn;
    private GameState state;
    private PlayerLobby thisPlayer;
    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.thisPlayer=thisPlayer;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.state=gameState;
    }

    @Override
    public void showStartupScreen(boolean isSocket, String ip, int port) {

    }

    @Override
    public void showRooms(List<RoomIF> rooms) {

    }

    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {

    }

    @Override
    public void showStartGame(GameState state) {

    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void setRoom(RoomIF room) {

    }

    @Override
    public void showPlayedStarter(PlayerLobby player) {

    }

    @Override
    public void showChosenPersonalObjective(PlayerLobby player) {

    }

    @Override
    public void showInGame() {

    }

    @Override
    public void showPlayedCard(PlayerLobby player, Coordinates coord) {

    }

    @Override
    public void showPickedCard(PlayerLobby player) {

    }

    @Override
    public void showNextTurn() {

    }


    @Override
    public void showPlayerDisconnected(PlayerLobby player) {

    }

    @Override
    public void showPlayerReconnected(PlayerLobby player) {

    }
    @Override
    public synchronized void showFinalPhase() {

    }

    @Override
    public synchronized void showUpdatePoints() {
        for(PlayerLobby playerLobby : state.getPlayers()){
            pointsTable.getItems().add(playerLobby);
        }

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("Nickname"));
        pointsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(state.getPlayerState(cellData.getValue()).getPoints())));

//        pointsColumn.setCellFactory(column -> new TableCell<PlayerLobby, String>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                if (!(item == null || empty)) {
//                    Label label = new Label(String.valueOf(state.getPlayerState(getTableView().getItems().get(getIndex())).getPoints()));
//                    setGraphic(label);
//                }
//            }
//        });
    }

    @Override
    public synchronized void showWinner() {
        System.out.println("winner");
        if(thisPlayer.equals(state.getWinner()))
            winnerText.setText("You have won the game");
        else
            winnerText.setText(state.getWinner().getNickname()+" won the game");
    }

    @Override
    public synchronized void showEndGame() {

    }




}
