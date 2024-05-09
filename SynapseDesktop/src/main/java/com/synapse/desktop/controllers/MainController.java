package com.synapse.desktop.controllers;

import com.synapse.core.experimentation.ExperimentParameters;
import com.synapse.core.training.TrainingResult;
import com.synapse.desktop.SceneService;
import com.synapse.desktop.io.Extension;
import com.synapse.desktop.io.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements ItemController<Object> {

    @FXML
    private ImageView mainImage;

    @FXML
    void openTrainingResult() {
        try {
            TrainingResult result = new FileLoader(this.getStage()).loadOneObject(Extension.TRAIN_RES);
            if (result != null) {
                var sceneService = new SceneService<>(TrainingResultController.class);
                sceneService.getController().setTrainingResult(result);
                SceneService.switchScene(getStage(), sceneService.getNewScene());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void trainingFromFile() {
        try {
            ExperimentParameters experimentParameters = new FileLoader(this.getStage()).loadOneObject(Extension.EXP_PAR);
            if (experimentParameters != null) {
                var sceneService = new SceneService<>(TrainingPreparationController.class);
                sceneService.getController().setExperimentParameters(experimentParameters);
                SceneService.switchScene(getStage(), sceneService.getNewScene());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void trainNewNet() {
        var sceneService = new SceneService<>(TrainingPreparationController.class);
        SceneService.switchScene(getStage(), sceneService.getNewScene());
    }

    Stage getStage() {
        return (Stage) mainImage.getScene().getWindow();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Object getItem() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
