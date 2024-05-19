package com.synapse.desktop.controllers;

import com.synapse.core.activation.Activation;
import com.synapse.core.experimentation.ExperimentParameters;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.samples.InMemorySampleService;
import com.synapse.core.samples.SampleService;
import com.synapse.core.training.TrainingParameters;
import com.synapse.desktop.SceneService;
import com.synapse.desktop.io.Extension;
import com.synapse.desktop.io.FileSaver;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class TrainingPreparationController implements ItemController<ExperimentParameters> {

    public static final int EXPERIENCE_COUNT = 10;
    @FXML
    private SubScene sampleScene;
    @FXML
    private SubScene netScene;
    @FXML
    private SubScene activationScene;
    @FXML
    private SubScene rateScene;
    @FXML
    private SubScene paramsScene;
    @FXML
    private TreeView<String> resultTree;
    @FXML
    private TabPane tabs;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveParameteresButton;
    @FXML
    private Button startLearnButton;
    NetStructureController netStructureController;
    ActivationController activationController;
    RateController rateController;
    SampleController sampleController;
    TrainingParametersController parametersController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var dataService = new SceneService<>(SampleController.class);
        sampleScene.setRoot(dataService.getRoot());
        sampleController = dataService.getController();

        var netService = new SceneService<>(NetStructureController.class);
        netScene.setRoot(netService.getRoot());
        netStructureController = netService.getController();

        var activationService = new SceneService<>(ActivationController.class);
        activationScene.setRoot(activationService.getRoot());
        activationController = activationService.getController();

        var rateService = new SceneService<>(RateController.class);
        rateScene.setRoot(rateService.getRoot());
        rateController = rateService.getController();

        var paramsService = new SceneService<>(TrainingParametersController.class);
        paramsScene.setRoot(paramsService.getRoot());
        parametersController = paramsService.getController();

        analyze();

    }

    @FXML
    void back() {
        if (tabs.getSelectionModel().isSelected(0)) {
            var sceneService = new SceneService<>(MainController.class);
            SceneService.switchScene(getStage(), sceneService.getNewScene());
        } else {
            tabs.getSelectionModel().selectPrevious();
        }
    }

    @FXML
    void next() {
        if (tabs.getSelectionModel().isSelected(tabs.getTabs().size() - 1)) {
            ExperimentParameters experimentParameters = getItem();
            FileSaver.defaultSave(Extension.EXP_PAR, experimentParameters);
            var sceneService = new SceneService<>(TrainingProcessController.class);
            sceneService.getController().setExperimentParameters(experimentParameters);
            SceneService.switchScene(getStage(), sceneService.getNewScene());
        } else {
            netStructureController.setLayers(
                    sampleController.getInputLayerSize(),
                    sampleController.getOutputLayerSize());
            tabs.getSelectionModel().selectNext();
        }
    }

    @FXML
    void analyze() {
        TreeItem<String> root = new TreeItem<>("Параметры");
        root.setExpanded(true);

        root.getChildren().add(getBranch(sampleController));
        root.getChildren().add(getBranch(netStructureController));
        root.getChildren().add(getBranch(activationController));
        root.getChildren().add(getBranch(rateController));
        root.getChildren().add(getBranch(parametersController));
        resultTree.setRoot(root);

        setNext();
    }

    @FXML
    void saveParameters() {
        ExperimentParameters experimentParameters = getItem();
        new FileSaver(getStage().getOwner()).selectPathAndSaveObject(Extension.EXP_PAR, experimentParameters);
    }

    private TreeItem<String> getBranch(SampleController controller) {
        TreeItem<String> root = new TreeItem<>("Данные для обучения");
        root.setExpanded(true);
        if (!controller.isValid()) {
            root.getChildren().add(new TreeItem<>("Не все параметры введены корректно."));
        } else {
            InMemorySampleService item = (InMemorySampleService) controller.getItem();
            item.getReport().forEach(r -> root.getChildren().add(new TreeItem<>(r)));
        }

        return root;
    }

    private TreeItem<String> getBranch(TrainingParametersController controller) {
        TreeItem<String> root = new TreeItem<>("Параметры обучения");
        root.setExpanded(true);
        if (!controller.isValid()) {
            root.getChildren().add(new TreeItem<>("Не все параметры введены корректно."));
        } else {
            var item = controller.getItem();
            root.getChildren().add(new TreeItem<>("Размер пакета: %d".formatted(item.getBatchSize())));
            root.getChildren().add(new TreeItem<>("Максимальное количество эпох: %d".formatted(item.getMaxEpochsCount())));
            root.getChildren().add(new TreeItem<>("Минимальная ошибка обучения: %f".formatted(item.getErrorLimit())));
        }

        return root;
    }

    private TreeItem<String> getBranch(RateController controller) {
        TreeItem<String> root = new TreeItem<>("Скорость обучения");
        root.setExpanded(true);

        if (!controller.isValid()) {
            root.getChildren().add(new TreeItem<>("Не все параметры введены корректно."));
        } else {
            root.getChildren().add(new TreeItem<>("Функция скорости: %s".formatted(controller.getItemName())));
            root.getChildren().add(new TreeItem<>("Параметры: %s".formatted(Arrays.toString(controller.getItem().getFactors()))));
        }

        return root;
    }

    private TreeItem<String> getBranch(ActivationController controller) {
        TreeItem<String> root = new TreeItem<>("Функция активации");
        root.setExpanded(true);

        if (!controller.isValid()) {
            root.getChildren().add(new TreeItem<>("Не все параметры введены корректно."));
        } else {
            root.getChildren().add(new TreeItem<>("Функция активации: %s".formatted(controller.getItemName())));
            root.getChildren().add(new TreeItem<>("Параметр: %f".formatted(controller.getItem().getScale())));
        }

        return root;
    }

    private TreeItem<String> getBranch(NetStructureController controller) {
        TreeItem<String> root = new TreeItem<>("Структура сети");
        root.setExpanded(true);

        if (!controller.isValid()) {
            root.getChildren().add(new TreeItem<>("Не все параметры введены корректно."));
        } else {
            var item = controller.getItem();
            root.getChildren().add(new TreeItem<>("Входной слой: %d".formatted(item[0])));
            root.getChildren().add(new TreeItem<>("Скрытые слои: %s".formatted(Arrays.toString(Arrays.copyOfRange(item, 1, item.length - 1)))));
            root.getChildren().add(new TreeItem<>("Выходной слой: %d".formatted(item[item.length - 1])));
        }

        return root;
    }


    private void setNext() {
        String text = "";
        boolean disable = true;

        if (sampleController.isValid() && netStructureController.isValid() && activationController.isValid()
                && rateController.isValid() && parametersController.isValid()) {
            if (sampleController.getInputLayerSize() != netStructureController.getInputLayerSize()) {
                text += "У данных обучения и структуры сети не совпадают входные размеры.\n";
            }
            if (sampleController.getOutputLayerSize() != netStructureController.getOutputLayerSize()) {
                text += "У данных обучения и структуры сети не совпадают выходные размеры.";
            }
            disable = false;
        }

        startLearnButton.setDisable(disable);
        saveParameteresButton.setDisable(disable);
        errorLabel.setText(text);
    }

    private Stage getStage() {
        return (Stage) tabs.getScene().getWindow();
    }

    @Override
    public boolean isValid() {
        return sampleController.isValid() && netStructureController.isValid() && activationController.isValid()
                && rateController.isValid() && parametersController.isValid() &&
                sampleController.getInputLayerSize() != netStructureController.getInputLayerSize() &&
                sampleController.getOutputLayerSize() != netStructureController.getOutputLayerSize();
    }

    @Override
    public ExperimentParameters getItem() {
        SampleService sampleService = sampleController.getItem();

        NetParameters netParameters = new NetParameters();
        netParameters.setLayerSizes(netStructureController.getItem());
        netParameters.setActivation(Activation.arrayOf(activationController.getItem(), netParameters.getLayerSizes().length));

        TrainingParameters trainingParameters = parametersController.getItem();
        trainingParameters.setRate(rateController.getItem());

        ExperimentParameters parameters = new ExperimentParameters();
        parameters.setNetParameters(netParameters);
        parameters.setSampleService(sampleService);
        parameters.setTrainingParameters(trainingParameters);

        return parameters;
    }

    public void setExperimentParameters(ExperimentParameters experimentParameters) {
        NetParameters netParameters = experimentParameters.getNetParameters();
        TrainingParameters trainingParameters = experimentParameters.getTrainingParameters();
        netStructureController.setLayers(netParameters.getLayerSizes());
        activationController.setActivation(netParameters.getActivation()[0]);
        parametersController.setTrainingParameters(trainingParameters);
    }
}

