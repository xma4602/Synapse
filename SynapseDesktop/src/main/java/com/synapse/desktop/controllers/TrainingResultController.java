package com.synapse.desktop.controllers;

import com.synapse.core.training.TrainingResult;
import com.synapse.desktop.io.Extension;
import com.synapse.desktop.io.FileLoader;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;

public class TrainingResultController implements ItemController<TrainingResult> {
    @FXML
    private LineChart<Number, Number> chart;
    @FXML
    private ToggleButton trainButton;
    @FXML
    private ToggleButton testButton;
    @FXML
    private Label learnMaxError;
    @FXML
    private Label learnMinError;
    @FXML
    private Label learnAverageError;
    @FXML
    private Label testAverageError;
    @FXML
    private Label testMaxError;
    @FXML
    private Label testMinError;
    @FXML
    private Label reason;
    @FXML
    private Label time;
    @FXML
    private Label epochs;

    private TrainingResult trainingResult;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String path = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\Synapse\\SynapseData\\cifar-10\\results\\result.train.res";
        File file = Path.of(path).toFile();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            TrainingResult result = (TrainingResult) in.readObject();
            setTrainingResult(result);
//        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTrainingResult(TrainingResult trainingResult) {
        this.trainingResult = trainingResult;
        drawCharts();
        setValues();
    }

    @FXML
    private void drawCharts() {
        if (trainingResult != null) {
            chart.getData().clear();
            int k = trainingResult.getTrainingErrors().size() / trainingResult.getTestingErrors().size();

            if (trainButton.isSelected()) {
                setChart(trainingResult.getTrainingErrors(), 1, "Обучение");
                setChart(trainingResult.getAverageTrainingErrors(), 1, "Обучение (тренд)");
            }
            if (testButton.isSelected()) {
                setChart(trainingResult.getTestingErrors(), k, "Тестирование (ошибка)");
//                setChart(trainingResult.getTestingPercents(), k, "Тестирование (% правильной классификации)");
            }
        }
    }

    private void setChart(List<Double> errors, int k, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < errors.size(); i++) {
            series.getData().add(new XYChart.Data<>(i * k, errors.get(i)));
        }
        chart.getData().add(series);
    }

    private void setValues() {
        if (trainingResult != null) {
            learnMaxError.setText("Макс. ошибка: " + trainingResult.getTrainingErrorsStatistics().getMax());
            learnMinError.setText("Мин. ошибка: " + trainingResult.getTrainingErrorsStatistics().getMin());
            learnAverageError.setText("Ср. ошибка: " + trainingResult.getTrainingErrorsStatistics().getAverage());

            testMaxError.setText("Макс. ошибка: " + trainingResult.getTestingErrorsStatistics().getMax());
            testMinError.setText("Мин. ошибка: " + trainingResult.getTestingErrorsStatistics().getMin());
            testAverageError.setText("Ср. ошибка: " + trainingResult.getTestingErrorsStatistics().getAverage());

            epochs.setText("Кол-во эпох: " + trainingResult.getEpochCount());
            reason.setText("Причина остановки: " + trainingResult.getStopReason().getDescription());
            time.setText("Время: " + format(trainingResult.getDuration()));
        }
    }

    private String format(Duration duration) {
        if (duration.toHours() > 0) {
            return "%f.2 минут".formatted(((double) duration.toMinutes() / 60));
        }
        if (duration.toMinutes() > 0) {
            return "%d минут".formatted(duration.toMinutes());
        }
        if (duration.toSeconds() > 0) {
            return "%d секунд".formatted(duration.toMinutes());
        }

        return "%d миллисекунд".formatted(duration.toMillis());
    }

    @FXML
    void selectFile() {
        try {
            TrainingResult result = new FileLoader(chart.getScene().getWindow()).loadOneObject(Extension.TRAIN_RES);
            setTrainingResult(result);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isValid() {
        return trainingResult != null;
    }

    @Override
    public TrainingResult getItem() {
        return trainingResult;
    }

}
