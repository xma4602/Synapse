package com.synapse.desktop;

import com.synapse.desktop.controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class SceneService<T extends ItemController<?>>  {
    private static final String ROOT = "descriptors/";
    private Parent parent;
    private T controller;

    private static final Map<Class<?>, String> files = new HashMap<>();

    static {
        files.put(MainController.class, "main.fxml");
        files.put(ActivationController.class, "activation.fxml");
        files.put(NetStructureController.class, "net-structure.fxml");
        files.put(TrainingParametersController.class, "training-parameters.fxml");
        files.put(TrainingResultController.class, "training-result.fxml");
        files.put(TrainingPreparationController.class, "training-preparation.fxml");
        files.put(TrainingProcessController.class, "training-process.fxml");

        files.put(RateController.class, "rate.fxml");
        files.put(SampleController.class, "samples.fxml");
    }

    @SneakyThrows
    public SceneService(Class<T> clas) {
        String file = ROOT + files.get(clas);
        FXMLLoader loader = new FXMLLoader(SceneService.class.getResource(file));
        parent = loader.load();
        controller = loader.getController();
    }

    public Parent getRoot() {
        return parent;
    }
    public Scene getNewScene() {
        return new Scene(parent);
    }

    public T getController() {
        return controller;
    }

    public static void switchScene(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
