module com.mmorano.crolympics {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires javafx.base;

    opens com.mmorano.crolympics to javafx.fxml;
    exports com.mmorano.crolympics;
}