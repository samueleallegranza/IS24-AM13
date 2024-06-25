package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.ParametersClient;
import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.PlayerLobby;
import it.polimi.ingsw.am13.model.player.Token;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

/**
 * Controller of the 'Rooms' scene, where the rooms are listed and the user can create/join/reconnect to a room
 */
public class ViewGUIControllerRooms extends ViewGUIController {

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


    @Override
    public void setThisPlayer(PlayerLobby player) {
    }
    @Override
    public void setGameState(GameState state) {
    }
    @Override
    public String getSceneTitle() {
        return "Codex Rooms";
    }

    @Override
    public void showException(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @Override
    public void showPlayerDisconnected(PlayerLobby player) {
    }
    @Override
    public void showPlayerReconnected(PlayerLobby player) {
    }

    /**
     * Force closing the app. It should be used to end the app for anomalous reasons
     */
    @Override
    public void forceCloseApp() {
        // I assume that an error message is already shown in an exception
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> stage.close());
    }


    public void showStartupScreen() {
        nPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4, 2));
        if(colorBox.getItems().isEmpty()) {
            colorBox.getItems().addAll(ColorToken.values());
        }
    }

    /**
     * Create a color object, choosing the color according to the given token
     * @param token Token used to choose the color
     * @return Color corresponding to the token
     */
    private Color chooseColorByToken(Token token) {
        return switch (token.getColor()) {
            case RED -> new Color(218/255.0,19/255.0,19/255.0,1);
            case BLUE -> new Color(19/255.0,63/255.0,168/255.0,1);
            case GREEN -> new Color(44/255.0,129/255.0,21/255.0,1);
            case YELLOW -> new Color(204/255.0,139/255.0,14/255.0,1);
        };
    }

    /**
     * Generates the string for the one of the 4 player columns for the rooms table.
     * It shows the player's nickname and if they are disconnected
     * @param room Room of the considered row
     * @param col Index of the column. Only 0, 1, 2, 3 is accepted
     * @return String for the player. "-" if the column exceeds the number of players
     */
    private String createShownPlayerString(RoomIF room, int col) {
        try {
            PlayerLobby player = room.getPlayersInGame().get(col);
            boolean isConnected = room.getPlayers().contains(player);

            return String.format("%s%s",
                    player.getNickname(),
                    isConnected ? "" : " (⚠)");
        } catch (IndexOutOfBoundsException e) {
            return col<room.getnPlayersTarget() ? "-" : "X";
        }
    }

    public void showRooms(List<RoomIF> rooms) {
        roomsTable.getItems().clear();
        for (RoomIF room : rooms) {
            roomsTable.getItems().add(room);
        }
        roomId.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        roomStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isGameStarted() ? "started" : "waiting"));

        //TODO: se va bene a tutti showare così i giocatori, togli codice commentato

//        roomP1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayers().isEmpty() ?
//                "" : cellData.getValue().getPlayers().getFirst().getNickname()));
        roomP1.setCellValueFactory(cellData -> new SimpleStringProperty(createShownPlayerString(cellData.getValue(), 0)));
        roomP1.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (!(item == null || empty) && !getTableView().getItems().get(getIndex()).getPlayersInGame().isEmpty()) {
                    Color color = chooseColorByToken(getTableView().getItems().get(getIndex()).getPlayersInGame().getFirst().getToken());
//                    Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayersInGame().getFirst().getToken().getColor().name());
                    Label label = new Label(item);
                    label.setTextFill(color);
                    setGraphic(label);
                }
            }
        });

//        roomP2.setCellValueFactory(cellData -> {
//            try {
//                return new SimpleStringProperty(cellData.getValue().getPlayers().get(1).getNickname());
//            } catch (IndexOutOfBoundsException e) {
//                return new SimpleStringProperty("-");
//            }
//        });
        roomP2.setCellValueFactory(cellData -> new SimpleStringProperty(createShownPlayerString(cellData.getValue(), 1)));
        roomP2.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (getTableView().getItems().get(getIndex()).getPlayersInGame().size() > 1) {
                        Color color = chooseColorByToken(getTableView().getItems().get(getIndex()).getPlayersInGame().get(1).getToken());
//                        Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayersInGame().get(1).getToken().getColor().name());
                        setTextFill(color);
                    }
                }
            }
        });

//        roomP3.setCellValueFactory(cellData -> {
//            try {
//                return new SimpleStringProperty(cellData.getValue().getPlayers().get(2).getNickname());
//            } catch (IndexOutOfBoundsException e) {
//                return new SimpleStringProperty("-");
//            }
//        });
        roomP3.setCellValueFactory(cellData -> new SimpleStringProperty(createShownPlayerString(cellData.getValue(), 2)));
        roomP3.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (getTableView().getItems().get(getIndex()).getPlayersInGame().size() > 2) {
                        Color color = chooseColorByToken(getTableView().getItems().get(getIndex()).getPlayersInGame().get(2).getToken());
//                        Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayersInGame().get(2).getToken().getColor().name());
                        setTextFill(color);
                    }
                }
            }
        });

//        roomP4.setCellValueFactory(cellData -> {
//            try {
//                return new SimpleStringProperty(cellData.getValue().getPlayers().get(3).getNickname());
//            } catch (IndexOutOfBoundsException e) {
//                return new SimpleStringProperty("-");
//            }
//        });
        roomP4.setCellValueFactory(cellData -> new SimpleStringProperty(createShownPlayerString(cellData.getValue(), 3)));
        roomP4.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (getTableView().getItems().get(getIndex()).getPlayersInGame().size() > 3) {
                        Color color = chooseColorByToken(getTableView().getItems().get(getIndex()).getPlayersInGame().get(3).getToken());
//                        Color color = Color.valueOf(getTableView().getItems().get(getIndex()).getPlayersInGame().get(3).getToken().getColor().name());
                        setTextFill(color);
                    }
                }
            }
        });

        roomNP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayers().size() + "/" + cellData.getValue().getnPlayersTarget()));

        if(ParametersClient.SKIP_ROOM) {
            boolean noGameToComplete=true;
            for(RoomIF room1 :rooms)
                if(!room1.isGameStarted())
                    noGameToComplete=false;
            if(noGameToComplete)
                networkHandler.createRoom(findFreeNickname(rooms), new Token(ColorToken.RED), ParametersClient.DEBUG_NPLAYERS);
            else {
                RoomIF room = rooms.getFirst();
                for(RoomIF room1 :rooms)
                    if(!room1.isGameStarted())
                        room = room1;
                String nickname=findFreeNickname(rooms);
                switch (room.getPlayers().size()) {
                    case 1 -> networkHandler.joinRoom(nickname, new Token(ColorToken.BLUE), room.getGameId());
                    case 2 -> networkHandler.joinRoom(nickname, new Token(ColorToken.GREEN), room.getGameId());
                    case 3 -> networkHandler.joinRoom(nickname, new Token(ColorToken.YELLOW), room.getGameId());
                    default -> throw new RuntimeException();
                }
            }
        }
    }

    private String findFreeNickname(List<RoomIF> rooms){
        List<String> nicknames = Arrays.asList("Harry","Hermione", "Ron", "Voldemort", "Dumbledore", "Hagrid", "Sirius",
                "Snake", "Weirdeye", "Pachoc", "Draco", "Lucius", "Bella", "Luna", "Cedric", "Umbridge", "McGrannit",
                "Fred", "George", "Dobby");
        String nickname="niiiii";       //TODO: sono confuso...
        for (String s : nicknames) {
            boolean notContained = true;
            for(RoomIF room1 : rooms)
                for (PlayerLobby player : room1.getPlayers())
                    if (s.equals(player.getNickname())) {
                        notContained = false;
                        break;
                    }
            if (notContained) {
                nickname = s;
                break;
            }
        }
        return nickname;
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

    @FXML
    public void onReconnectMatchButtonClick() {
        if (nicknameField.getText().isEmpty()){
            errorAlert("Please insert a nickname");
        }else if (colorBox.getValue() == null){
            errorAlert("Please select a color");
        }else{
            String nickname = nicknameField.getText();
            ColorToken color = colorBox.getValue();
            networkHandler.reconnect(nickname, new Token(color));
        }
    }

    private void errorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
