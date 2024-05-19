package com.synapse.desktop.controllers;

import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.desktop.SceneService;
import com.synapse.desktop.io.Extension;
import com.synapse.desktop.io.FileLoader;
import com.synapse.desktop.io.FileSaver;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentResultController implements ItemController<ExperimentResult> {


    @FXML
    private TabPane tabPane;
    private ExperimentResult experimentResult;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setExperimentResult(ExperimentResult experimentResult) {
        if (experimentResult != null) {
            this.experimentResult = experimentResult;
            setExperiences();
        }
    }

    private void setExperiences() {
        tabPane.getTabs().clear();
        for (var entry : experimentResult.getExperiences().entrySet()) {
            String title = "Эксперимент " + (entry.getKey() + 1);
            var sceneService = new SceneService<>(TrainingResultController.class);
            sceneService.getController().setExperimentParameters(entry.getValue().getKey());
            sceneService.getController().setTrainingResult(entry.getValue().getValue());
            Parent root = sceneService.getRoot();
            tabPane.getTabs().add(new Tab(title, root));
        }
    }


    @FXML
    void openResult() {
        try {
            ExperimentResult result = new FileLoader(getStage()).loadOneObject(Extension.EXP_RES);
            setExperimentResult(result);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void saveResult() {
        new FileSaver(getStage()).selectPathAndSaveObject(Extension.EXP_RES, experimentResult);
    }

    @FXML
    void toMain() {
        var sceneService = new SceneService<>(MainController.class);
        SceneService.switchScene(getStage(), sceneService.getNewScene());
    }

    private Stage getStage() {
        return (Stage) tabPane.getScene().getWindow();
    }

    @Override
    public boolean isValid() {
        return experimentResult != null;
    }

    @Override
    public ExperimentResult getItem() {
        return experimentResult;
    }


}
