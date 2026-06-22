package com.mmorano.crolympics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class CrolympicsApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CrolympicsApplication.class.getResource("crolympics-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 750);
        CrolympicsController controller = fxmlLoader.getController();
        stage.setTitle("CROLYMPICS (Tribunes Technical Solutions LLC)");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
        //controller.printNodeDetails(scene.getRoot(), "");
    }
}
