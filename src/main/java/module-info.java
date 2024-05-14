module it.polimi.ingsw.am13 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;

    opens it.polimi.ingsw.am13 to javafx.fxml;

    exports it.polimi.ingsw.am13.network.rmi to java.rmi;
    exports it.polimi.ingsw.am13;
}