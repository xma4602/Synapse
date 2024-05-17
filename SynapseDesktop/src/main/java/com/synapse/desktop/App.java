package com.synapse.desktop;

import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.data.learning.LearningDigiface;
import com.synapse.desktop.controllers.ExperimentResultController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    final String icon = "images/synapse-square.jpg";

    @Override
    public void start(Stage stage) throws IOException {
        ExperimentResult experimentResult = LearningDigiface.learn();

        var sceneService = new SceneService<>(ExperimentResultController.class);
        sceneService.getController().setExperimentResult(experimentResult);

        stage.setScene(sceneService.getNewScene());
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResource(icon)).openStream()));
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }
}