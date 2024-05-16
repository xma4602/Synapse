package com.synapse.desktop.controllers;

import com.synapse.core.experimentation.ExperimentParameters;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.training.TrainingParameters;
import com.synapse.core.training.TrainingResult;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.ResourceBundle;

public class TrainingResultController implements ItemController<TrainingResult> {
    @FXML
    private Label activation;

    @FXML
    private Label batchSize;

    @FXML
    private LineChart<Number, Number> errorsChart;
    @FXML
    private LineChart<Number, Number> percentChart;

    @FXML
    private Label epochs;

    @FXML
    private Label errorLimit;

    @FXML
    private Label layers;

    @FXML
    private Label learnAverageError;

    @FXML
    private Label learnMaxError;

    @FXML
    private Label learnMinError;

    @FXML
    private Label maxEpoch;

    @FXML
    private Label rate;

    @FXML
    private Label reason;

    @FXML
    private Label testAverageError;

    @FXML
    private Label testAveragePercent;

    @FXML
    private Label testMaxError;

    @FXML
    private Label testMaxPercent;

    @FXML
    private Label testMinError;

    @FXML
    private Label testMinPercent;

    @FXML
    private Label time;
    private TrainingResult trainingResult;
    private ExperimentParameters experimentParameters;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void setExperimentParameters(ExperimentParameters experimentParameters) {
        this.experimentParameters = experimentParameters;
        if (experimentParameters != null) {
            setParams();
        }
    }

    public void setTrainingResult(TrainingResult trainingResult) {
        if (trainingResult != null) {
            this.trainingResult = trainingResult;
            drawCharts();
            setResults();
        }
    }

    @FXML
    private void drawCharts() {
        errorsChart.getData().clear();
        percentChart.getData().clear();
        double k = trainingResult.getTrainingErrors().size() * 1.0 / trainingResult.getTestingErrors().size();

//        addChart(trainingResult.getTrainingErrors(), 1, "Обучение");
        addChart(errorsChart, trainingResult.getAverageTrainingErrors(), 1, "Тренд ошибки обучения");
        addChart(errorsChart, trainingResult.getTestingErrors(), k, "Ошибка тестирования");
        addChart(percentChart, trainingResult.getTestingPercents(), k, "Тестирование (% правильной классификации)");
    }

    private void addChart(LineChart<Number, Number> chart, List<Double> errors, double k, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < errors.size(); i++) {
            series.getData().add(new XYChart.Data<>((int) (i * k), errors.get(i)));
        }
        chart.getData().add(series);
    }

    private void setResults() {
        if (trainingResult != null) {
            DoubleSummaryStatistics trainingErrorsStatistics = trainingResult.getTrainingErrorsStatistics();
            learnMaxError.setText("Макс. ошибка: %.5f".formatted(trainingErrorsStatistics.getMax()));
            learnMinError.setText("Мин.  ошибка: %.5f".formatted(trainingErrorsStatistics.getMin()));
            learnAverageError.setText("Сред. ошибка: %.5f".formatted(trainingErrorsStatistics.getAverage()));

            DoubleSummaryStatistics testingErrorsStatistics = trainingResult.getTestingErrorsStatistics();
            testMaxError.setText("Макс. ошибка: %.5f".formatted(testingErrorsStatistics.getMax()));
            testMinError.setText("Мин.  ошибка: %.5f".formatted(testingErrorsStatistics.getMin()));
            testAverageError.setText("Сред. ошибка: %.5f".formatted(testingErrorsStatistics.getAverage()));

            DoubleSummaryStatistics testingPercentsStatistics = trainingResult.getTestingPercentsStatistics();
            testMaxPercent.setText("Макс. процент: %.5f%%".formatted(testingPercentsStatistics.getMax()));
            testMinPercent.setText("Мин.  процент: %.5f%%".formatted(testingPercentsStatistics.getMin()));
            testAveragePercent.setText("Сред. процент: %.5f%%".formatted(testingPercentsStatistics.getAverage()));

            epochs.setText("Количество проведенных эпох: " + trainingResult.getEpochCount());
            reason.setText("Причина остановки: " + trainingResult.getStopReason().getDescription());
            Duration duration = trainingResult.getDuration();
            time.setText("Время: %dч. %dм. %dс. %dмс.".formatted(
                    duration.toHoursPart(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart(),
                    duration.toMillisPart()
            ));
        }
    }


    private void setParams() {
        TrainingParameters trainingParameters = experimentParameters.getTrainingParameters();
        maxEpoch.setText("Макс. кол-во эпох: " + trainingParameters.getMaxEpochsCount());
        batchSize.setText("Размер пакета: " + trainingParameters.getBatchSize());
        rate.setText("Ф-я скорости обучения: " + trainingParameters.getRate());

        NetParameters netParameters = experimentParameters.getNetParameters();
        layers.setText("Структура слоев: " + Arrays.toString(netParameters.getLayerSizes()));
        activation.setText("Ф-я активации: " + netParameters.getActivations()[0]);
    }


    @FXML
    void selectFile() {

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
