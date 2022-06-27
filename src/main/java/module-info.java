module com.morticia.compsim {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;

    opens com.morticia.compsim to javafx.fxml;
    exports com.morticia.compsim;
    exports com.morticia.compsim.UI;
    opens com.morticia.compsim.UI to javafx.fxml;
}