module it.polimi.ingsw.am13 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.desktop;

    opens it.polimi.ingsw.am13 to javafx.fxml;
    opens it.polimi.ingsw.am13.controller to javafx.base;
    opens it.polimi.ingsw.am13.client.view.gui to javafx.fxml;
//    opens it.polimi.ingsw.am13.controller to javafx.base;
    opens it.polimi.ingsw.am13.model.player to javafx.base;

    exports it.polimi.ingsw.am13.client.network.rmi to java.rmi;
    exports it.polimi.ingsw.am13.network.rmi to java.rmi;
    exports it.polimi.ingsw.am13;
    exports it.polimi.ingsw.am13.model.card;
    exports it.polimi.ingsw.am13.model.exceptions;
    exports it.polimi.ingsw.am13.model.player;
    exports it.polimi.ingsw.am13.controller;
    exports it.polimi.ingsw.am13.model;
    exports it.polimi.ingsw.am13.client.view;
    exports it.polimi.ingsw.am13.client.gamestate;
    exports it.polimi.ingsw.am13.client.chat;
    exports it.polimi.ingsw.am13.client.network;
    exports it.polimi.ingsw.am13.client.view.tui.menu;
    exports it.polimi.ingsw.am13.model.card.points;

    exports it.polimi.ingsw.am13.client.view.gui to javafx.graphics;
    exports it.polimi.ingsw.am13.client.view.tui;
}