package com.synapse.desktop.controllers;

import com.synapse.core.rates.ConstantRate;
import com.synapse.core.rates.ExponentRate;
import com.synapse.core.rates.LinearRate;
import com.synapse.core.rates.Rate;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import lombok.Value;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RateController implements ItemController<Rate> {
    @FXML
    private ToggleSwitch constSwitch;
    @FXML
    private ToggleSwitch linearSwitch;
    @FXML
    private ToggleSwitch expSwitch;
    @FXML
    private Spinner<Double> constSpinner;
    @FXML
    private Spinner<Double> linearSpinner;
    @FXML
    private Spinner<Double> expSpinner1;
    @FXML
    private Spinner<Double> expSpinner2;
    @FXML
    private Label constErrorLabel;
    @FXML
    private Label linearErrorLabel;
    @FXML
    private Label expErrorLabel1;
    @FXML
    private Label expErrorLabel2;
    @FXML
    private LineChart<Double, Double> chart;

    private List<RateComplex> complexes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        complexes.add(new RateComplex("Постоянная",
                constSwitch,
                new ConstantRate(1),
                List.of(constSpinner),
                List.of(constErrorLabel)));
        complexes.add(new RateComplex("Линейная",
                linearSwitch,
                new LinearRate(1),
                List.of(linearSpinner),
                List.of(linearErrorLabel)));
        complexes.add(new RateComplex("Экспоненциальная",
                expSwitch,
                new ExponentRate(1, 1),
                List.of(expSpinner1, expSpinner2),
                List.of(expErrorLabel1, expErrorLabel2)));

        constErrorLabel.setText("");
        linearErrorLabel.setText("");
        expErrorLabel1.setText("");
        expErrorLabel2.setText("");

        setSpinner(constSpinner, constErrorLabel, 0.3);
        setSpinner(linearSpinner, linearErrorLabel, 1);
        setSpinner(expSpinner1, expErrorLabel1, 1);
        setSpinner(expSpinner2, expErrorLabel2, 1);

        drawCharts();
    }

    private void setSpinner(Spinner<Double> spinner, Label errorLabel, double value) {
        var factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, value, 0.1);
        spinner.setValueFactory(factory);

        spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue <= 4.9e-324) {
                errorLabel.setText("Значение должно быть действительным положительным числом");
            } else {
                errorLabel.setText("");
                drawCharts();
            }
        });
    }

    private void drawCharts() {
        chart.getData().clear();
        for (var rate : complexes) {
            if (rate.isSelected()) {
                drawChart(rate.getRate(), rate.getName());
            }
        }
    }

    private void drawChart(Rate rate, String name) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        series.setName(name);
        for (int x = 1; x < 100; x++) {
            series.getData().add(new XYChart.Data<>((double) x, rate.apply(x)));
        }
        chart.getData().add(series);
    }

    @FXML
    private void switchButton() {
        drawCharts();
    }

    @Override
    public boolean isValid() {
        return complexes.stream()
                .filter(RateComplex::isSelected)
                .count() == 1;

    }

    @Override
    public Rate getItem() {
        return complexes.stream()
                .filter(RateComplex::isSelected)
                .findFirst().get().getRate();
    }

    public String getItemName() {
        return complexes.stream()
                .filter(RateComplex::isSelected)
                .findFirst().get().getName();
    }

    @Value
    private static class RateComplex {
        String name;
        ToggleSwitch toggleSwitch;
        Rate rate;
        List<Spinner<Double>> spinners;
        List<Label> labels;

        public boolean isSelected() {
            return toggleSwitch.isSelected();
        }

        public Rate getRate() {
            rate.setFactors(
                    spinners.stream().mapToDouble(Spinner::getValue).toArray()
            );
            return rate;
        }
    }

}
