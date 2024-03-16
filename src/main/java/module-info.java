module it.polimi.ingsw.am13 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.polimi.ingsw.am13 to javafx.fxml;
    exports it.polimi.ingsw.am13;
}