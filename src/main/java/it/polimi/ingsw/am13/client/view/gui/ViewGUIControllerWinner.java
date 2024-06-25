package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * Controller of 'Winner' scene, where the final points and the winner are shown
 */
public class ViewGUIControllerWinner extends ViewGUIController{

    @FXML
    public Label winnerText;
    @FXML
    public TableView<PlayerLobby> pointsTable;
    @FXML
    public TableColumn<PlayerLobby,String> playerColumn;
    @FXML
    public TableColumn<PlayerLobby,String> pointsColumn;

    /**
     * State of the game
     */
    private GameState state;

    /**
     * Player associated to the client the GUI was created by
     */
    private PlayerLobby thisPlayer;

    /**
     * Sets the player associated to the client the GUI was created by
     * @param thisPlayer Player associated to the client the GUI was created by
     */
    @Override
    public void setThisPlayer(PlayerLobby thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    /**
     * Sets the state of the game
     * @param gameState State of the game
     */
    @Override
    public void setGameState(GameState gameState) {
        this.state = gameState;
    }

    /**
     * @return Title of the screen
     */
    @Override
    public String getSceneTitle() {
        return "Codex Naturalis - Winner screen";
    }

    /**
     * This method cannot be called
     * @param e Exception to be shown
     */
    @Override
    public void showException(Exception e) {
    }
    /**
     * It should never be called, not implemented
     */
    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
    }
    /**
     * It should never be called, not implemented
     */
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
    }

    /**
     * This method cannot be called
     */
    @Override
    public void forceCloseApp() {
    }

    /**
     * Shows a table of the players with their final points, printing the winner / winners.
     */
    public synchronized void showWinner() {
        for (PlayerLobby playerLobby : state.getPlayers()) {
            pointsTable.getItems().add(playerLobby);
        }

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        pointsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(state.getPlayerState(cellData.getValue()).getPoints())));

        // Center text in playerColumn
        playerColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlayerLobby, String> call(TableColumn<PlayerLobby, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });

        // Center text in pointsColumn
        pointsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlayerLobby, String> call(TableColumn<PlayerLobby, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });

        pointsTable.setFixedCellSize(45);
        double totalHeight = state.getPlayers().size() * pointsTable.getFixedCellSize();
        totalHeight += 60;  // header height, could be necessary to arrange better the value
        pointsTable.setPrefHeight(totalHeight);

        if (state.getWinner().size() == 1 && thisPlayer.equals(state.getWinner().getFirst())) {
            winnerText.setText(state.getWinner().getFirst().getNickname() + " won the game");
        } else {
            StringBuilder winnerStr = new StringBuilder();
            for (int i = 0; i < state.getWinner().size(); i++) {
                winnerStr.append(state.getWinner().get(i).getNickname());
                if (i != state.getWinner().size() - 1)
                    winnerStr.append(", ");
            }
            winnerStr.append(" won the game");
            winnerText.setText(winnerStr.toString());
        }
    }
}
