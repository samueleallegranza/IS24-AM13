package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

public class ViewGUIControllerRooms extends ViewGUIController {
    @FXML
    private Label startupText;
    @FXML
    private TableView<RoomIF> roomsTable;
    @FXML
    private TextField nicknameField;
    @FXML
    private ChoiceBox<ColorToken> colorBox;
    @FXML
    private TableColumn<RoomIF, Integer> roomId;
    @FXML
    private TableColumn<RoomIF, String> roomStatus;
    @FXML
    private TableColumn<RoomIF, String> roomP1;
    @FXML
    private TableColumn<RoomIF, String> roomP2;
    @FXML
    private TableColumn<RoomIF, String> roomP3;
    @FXML
    private TableColumn<RoomIF, String> roomP4;
    @FXML
    private TableColumn<RoomIF, String> roomNP;
    @FXML
    private Spinner<Integer> nPlayersSpinner;
    @FXML
    private Button createRoomButton;
    @FXML
    private Button joinRoomButton;
    @FXML
    private Button refreshRoomButton;

    @Override
    public void setPlayer(PlayerLobby player) {

    }

    @Override
    public void setGameState(GameState gameState) {

    }

    public void showStartupScreen(boolean isSocket, String ip, int port) {
        if(isSocket)
            startupText.setText("Welcome to Codex Naturalis! (connected via socket to "+ip+" "+port+")");
        else
            startupText.setText("Welcome to Codex Naturalis! (connected via RMI to "+ip+" "+port+")");
        nPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4, 2));
        colorBox.getItems().addAll(ColorToken.values());

    }
    public void showRooms(List<RoomIF> rooms) {
        roomsTable.getItems().clear();
        for (RoomIF room : rooms) {
            roomsTable.getItems().add(room);
        }
        roomId.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        roomStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isGameStarted() ? "started" : "waiting"));

        roomP1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayers().get(0).getNickname()));
        roomP1.setCellFactory(column -> new TableCell<RoomIF, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!(item == null || empty) && getTableView().getItems().get(getIndex()).getPlayers().size() > 0) {
                    Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayers().get(0).getToken().getColor().name());
                    Label label = new Label(item);
                    label.setTextFill(color);
                    setGraphic(label);
                }
            }
        });

        roomP2.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getPlayers().get(1).getNickname());
            } catch (IndexOutOfBoundsException e) {
                return new SimpleStringProperty("-");
            }
        });
        roomP2.setCellFactory(column -> new TableCell<RoomIF, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (getTableView().getItems().get(getIndex()).getPlayers().size() > 1) {
                        Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayers().get(1).getToken().getColor().name());
                        setTextFill(color);
                    }
                }
            }
        });

        roomP3.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getPlayers().get(2).getNickname());
            } catch (IndexOutOfBoundsException e) {
                return new SimpleStringProperty("-");
            }
        });
        roomP3.setCellFactory(column -> new TableCell<RoomIF, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (getTableView().getItems().get(getIndex()).getPlayers().size() > 2) {
                        Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayers().get(2).getToken().getColor().name());
                        setTextFill(color);
                    }
                }
            }
        });

        roomP4.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getPlayers().get(3).getNickname());
            } catch (IndexOutOfBoundsException e) {
                return new SimpleStringProperty("-");
            }
        });
        roomP4.setCellFactory(column -> new TableCell<RoomIF, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (getTableView().getItems().get(getIndex()).getPlayers().size() > 3) {
                        Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayers().get(3).getToken().getColor().name());
                        setTextFill(color);
                    }
                }
            }
        });

        roomNP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayers().size() + "/" + cellData.getValue().getnPlayersTarget()));
    }

    @Override
    public void showPlayerJoinedRoom(PlayerLobby player) {

    }

    @Override
    public void showStartGame(GameState state) {

    }

    @FXML
    public void onCreateRoomButtonClick(){
        if (nicknameField.getText().isEmpty()){
            errorAlert("Please insert a nickname");
        }else if (colorBox.getValue() == null){
            errorAlert("Please select a color");
        }else {
            String nickname = nicknameField.getText();
            ColorToken color = colorBox.getValue();
            int nPlayers = nPlayersSpinner.getValue();
            networkHandler.createRoom(nickname, new Token(color), nPlayers);
        }
    }

    @FXML
    public void onJoinRoomButtonClick(){
        if (nicknameField.getText().isEmpty()){
            errorAlert("Please insert a nickname");
        }else if(roomsTable.getSelectionModel().getSelectedItem() == null){
            errorAlert("Please select a room to join");
        }else if (colorBox.getValue() == null){
            errorAlert("Please select a color");
        }else{
            ColorToken color = colorBox.getValue();
            int gameId = roomsTable.getSelectionModel().getSelectedItem().getGameId();
            String nickname = nicknameField.getText();
            networkHandler.joinRoom(nickname, new Token(color), gameId);
        }
    }

    @FXML
    public void onRefreshRoomButtonClick(){
        networkHandler.getRooms();
    }

    public void showException(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @Override
    public void setGameId(int gameId) {

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

    private void errorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
