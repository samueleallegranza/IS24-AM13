package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewGUIControllerWinner extends ViewGUIController{

    @FXML
    public Label winnerText;
    @FXML
    public TableView<PlayerLobby> pointsTable;
    @FXML
    public TableColumn<PlayerLobby,String> playerColumn;
    @FXML
    public TableColumn<PlayerLobby,String> pointsColumn;

    private GameState state;
    private PlayerLobby thisPlayer;

    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.thisPlayer = thisPlayer;
    }
    @Override
    public void setGameState(GameState gameState) {
        this.state = gameState;
    }
    @Override
    public String getSceneTitle() {
        return "Winner screem";
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

    public synchronized void showWinner() {

        for(PlayerLobby playerLobby : state.getPlayers()) {
            pointsTable.getItems().add(playerLobby);
        }
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("Nickname"));
        pointsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(state.getPlayerState(cellData.getValue()).getPoints())));

        pointsTable.setFixedCellSize(45);
        double totalHeight = state.getPlayers().size() * pointsTable.getFixedCellSize();
        totalHeight += 42;      // header height, could be necessary to arrange better the value
        System.out.println(totalHeight);
        pointsTable.setPrefHeight(totalHeight);

        if(thisPlayer.equals(state.getWinner()))
            winnerText.setText("You have won the game");
        else
            winnerText.setText(state.getWinner().getNickname() + " won the game");
    }

    public synchronized void showEndGame() {
        //TODO: da implementare
    }
}
