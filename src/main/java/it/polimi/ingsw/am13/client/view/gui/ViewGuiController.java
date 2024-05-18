package it.polimi.ingsw.am13.client.view.gui;

import it.polimi.ingsw.am13.client.network.NetworkHandler;
import it.polimi.ingsw.am13.client.view.tui.menu.InvalidTUICommandException;
import it.polimi.ingsw.am13.controller.RoomIF;
import it.polimi.ingsw.am13.model.player.ColorToken;
import it.polimi.ingsw.am13.model.player.Token;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class ViewGuiController {
    private NetworkHandler networkHandler;
    private Stage stage;
    @FXML
    private Label networkText;
    @FXML
    private TableView<RoomIF> roomsTable;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }
    public void showStartupScreen(boolean isSocket, String ip, int port) {
        if(isSocket)
            networkText.setText("socket "+ip+" "+port);
    }
    public void showRooms(List<RoomIF> rooms) {
        for(RoomIF room : rooms){
            roomsTable.getItems().add(room);
            roomsTable.setRowFactory(roomButton -> {
                TableRow<RoomIF> row = new TableRow<>();
                row.setOnMouseClicked(mouseEvent -> {
                    TextField nicknameField = new TextField(), colorField = new TextField();
                    String nickname = nicknameField.getText();
                    String color = colorField.getText();
                    ColorToken token = null;
                    try {
                        token = ColorToken.valueOf(color.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        //throw new InvalidTUICommandException("Invalid token color. Please choose one of the following: red, blue, green, yellow");
                    }
                    networkHandler.joinRoom(nickname, new Token(token), room.getGameId());
                });
                return row;
            });
        }
    }
}
