module it.polimi.ingsw.am13 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.desktop;

    opens it.polimi.ingsw.am13 to javafx.fxml;
    opens it.polimi.ingsw.am13.client.view.gui to javafx.fxml;

    exports it.polimi.ingsw.am13.network.rmi to java.rmi;
    exports it.polimi.ingsw.am13;

    exports it.polimi.ingsw.am13.client.view.gui to javafx.graphics;
}