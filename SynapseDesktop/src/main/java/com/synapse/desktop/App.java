package com.synapse.desktop;

import com.synapse.desktop.controllers.TrainingResultController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    final String icon = "images/synapse-square.jpg";

    @Override
    public void start(Stage stage) throws IOException {
        var control = new SceneService<>(TrainingResultController.class);
        stage.setScene(control.getNewScene());
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResource(icon)).openStream()));
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch();
    }
}