package com.synapse.desktop.controllers;

import com.synapse.core.experimentation.ExperimentParameters;
import com.synapse.core.tools.Monitored;
import com.synapse.core.training.SimpleTeacher;
import com.synapse.core.training.Teacher;
import com.synapse.core.training.TrainingResult;
import com.synapse.desktop.SceneService;
import com.synapse.desktop.io.Extension;
import com.synapse.desktop.io.FileSaver;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class TrainingProcessController implements ItemController<TrainingResult> {

    @FXML
    private TreeTableView<MonitoredView> treeTable;
    MonitoredMock monitored = new MonitoredMock("0", 2);
    Teacher teacher = new SimpleTeacher();
    Timeline timeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeTableColumn<MonitoredView, String> column1 = new TreeTableColumn<>("Имя процесса");
        TreeTableColumn<MonitoredView, String> column2 = new TreeTableColumn<>("Прогресс процесса");
        column1.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        column2.setCellValueFactory(new TreeItemPropertyValueFactory<>("progress"));
        column1.setPrefWidth(100);
        column2.setPrefWidth(150);
        treeTable.getColumns().addAll(column1, column2);

        treeTable.setRoot(getRoot(new MonitoredView(monitored)));
        initTimer();
    }

    public void setExperimentParameters(ExperimentParameters experimentParameters) {
        teacher.setParameters(experimentParameters);
        Executors.callable(teacher);
        initTimer();
    }


    private void initTimer() {
        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event -> update()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void update() {
        if (monitored.getProgress() == 1.0) {
            timeline.stop();
            var service = new SceneService<>(TrainingResultController.class);
            TrainingResult trainingResult = teacher.getTrainingResult();
            FileSaver.defaultSave(Extension.TRAIN_RES, trainingResult);

            service.getController().setTrainingResult(trainingResult);
            SceneService.switchScene((Stage) treeTable.getScene().getWindow(), service.getNewScene());
        } else {
            treeTable.setRoot(getRoot(new MonitoredView(monitored)));
        }
    }

    public TreeItem<MonitoredView> getRoot(MonitoredView monitoredView) {
        TreeItem<MonitoredView> root = new TreeItem<>(monitoredView);
        root.setExpanded(true);
        List<TreeItem<MonitoredView>> list = monitoredView.getChildren().stream().map(this::getRoot).toList();
        root.getChildren().addAll(list);
        return root;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public TrainingResult getItem() {
        return teacher.getTrainingResult();
    }

    @Getter
    public static class MonitoredView {
        private final Monitored monitored;
        private String name;
        private String progress;

        public MonitoredView(Monitored monitored) {
            this.monitored = monitored;
            name = monitored.getProcessName();
            progress = String.format("%7.2f%%", monitored.getProgress() * 100);
        }

        public List<MonitoredView> getChildren() {
            List<MonitoredView> children = new ArrayList<>();
            monitored.getProcessComponents().forEach(monitored -> children.add(new MonitoredView(monitored)));
            return children;
        }
    }

    private static class MonitoredMock implements Monitored {

        private final String name;
        private List<MonitoredMock> children = new ArrayList<>();
        private Supplier<Double> progress;
        private int time = 0;

        public MonitoredMock(String name, int level) {
            this.name = name;
            if (level == 0) {
                progress = this::getTimer;
            } else {
                children.add(new MonitoredMock(name + "-0", level - 1));
                children.add(new MonitoredMock(name + "-1", level - 1));
                progress = () -> children.stream().mapToDouble(MonitoredMock::getProgress).sum() / children.size();
            }
        }

        private double getTimer() {
            time += Math.random() * 10;
            if (time > 1000) {
                return 1.0;
            } else {
                return time / 1000.0;
            }
        }

        @Override
        public String getProcessName() {
            return name;
        }

        @Override
        public double getProgress() {
            return progress.get();
        }

        @Override
        public Iterable<? extends Monitored> getProcessComponents() {
            return children;
        }
    }

}
