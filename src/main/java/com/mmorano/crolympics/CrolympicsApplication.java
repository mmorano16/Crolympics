package com.mmorano.crolympics;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class CrolympicsApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double usableWidth = visualBounds.getWidth();
        String fxmlFile = switch ((int) usableWidth) {
            case 1440, 1400 -> "crolympics-view-1440x900.fxml";
            default -> "crolympics-view-1920x1080.fxml";
        };//default to 1920x1080
        FXMLLoader fxmlLoader = new FXMLLoader(CrolympicsApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 900);
        stage.setTitle("CROLYMPICS (Tribunes Technical Solutions LLC)");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        //controller.printNodeDetails(scene.getRoot(), "");
    }
}
