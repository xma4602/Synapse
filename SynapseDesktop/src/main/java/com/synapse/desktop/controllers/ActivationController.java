package com.synapse.desktop.controllers;

import com.synapse.core.activation.*;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.DoubleFunction;
import java.util.stream.Collectors;

public class ActivationController implements ItemController<Activation> {

    @FXML
    private CheckBox atan;
    @FXML
    private CheckBox tanh;
    @FXML
    private CheckBox logistic;
    @FXML
    private CheckBox relu;
    @FXML
    private LineChart<Double, Double> activation_chart;
    @FXML
    private LineChart<Double, Double> deactivation_chart;
    @FXML
    private Label scaleLabel;
    @FXML
    private Slider scaleSlider;
    @FXML
    private Spinner<Double> max_scale;
    @FXML
    private Spinner<Double> min_scale;

    private final Map<CheckBox, Activation> activations = new HashMap<>();
    private double scaleValue = 1.0;
    private double step = 0.1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        activations.put(logistic, new ActivationLog(scaleValue));
        activations.put(tanh, new ActivationTanh(scaleValue));
        activations.put(atan, new ActivationAtan(scaleValue));
        activations.put(relu, new ActivationReLU(scaleValue));
        logistic.setSelected(true);

        setSpinner(min_scale, 0);
        setSpinner(max_scale, 1.0);

        scaleSlider.setValue(20);
        scaleSlider.valueProperty().addListener((observableValue, number, t1) -> updateScaleLabel());

        updateScaleLabel();
    }

    private void setSpinner(Spinner<Double> spinner, double value) {
        var factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(
                Double.MIN_VALUE, Double.MAX_VALUE, value, step);

        factory.valueProperty().addListener((observable, oldValue, newValue) -> {
            checkSpinnerValue(spinner);
            updateScaleLabel();
        });

        spinner.setValueFactory(factory);
    }

    private void checkSpinnerValue(Spinner<Double> spinner) {
        double min = min_scale.getValue();
        double max = max_scale.getValue();

        if (spinner == min_scale) {
            if (min >= max) {
                min = max - step > 0 ? max - step : 1e-6;
                min_scale.getValueFactory().setValue(min);
            }
        } else {
            if (min >= max) {
                max = min + step;
                max_scale.getValueFactory().setValue(max);
            }
        }
    }

    @FXML
    private void updateScaleLabel() {
        scaleValue = min_scale.getValue() + (max_scale.getValue() - min_scale.getValue()) * scaleSlider.getValue() / 100;
        scaleLabel.setText("%.5f".formatted(scaleValue));
        makeCharts(getSelectedCharts());
    }

    private Map<String, Activation> getSelectedCharts() {
        return activations.entrySet().stream()
                .filter(item -> item.getKey().isSelected())
                .collect(Collectors.toMap(
                        item -> item.getKey().getText(),
                        Map.Entry::getValue
                ));
    }

    private void makeCharts(Map<String, Activation> functions) {
        activation_chart.getData().clear();
        deactivation_chart.getData().clear();

        for (var function : functions.entrySet()) {
            makeChart(function.getKey(), function.getValue());
        }
    }

    private void makeChart(String name, Activation activation) {
        activation.setScale(scaleValue);
        makeGraphic(name, activation.getActivator(), activation_chart);
        makeGraphic(name + "'", activation.getDeactivator(), deactivation_chart);
    }

    private void makeGraphic(String name, DoubleFunction<Double> function, LineChart<Double, Double> chart) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        series.setName(name);

        double border = 50;
        double step = border / 50;

        for (double x = -border; x <= border; x += step) {
            series.getData().add(new XYChart.Data<>(x, function.apply(x)));
        }

        chart.getData().add(series);
    }

    @Override
    public boolean isValid() {
        return getSelectedCharts().size() == 1;
    }

    @Override
    public Activation getItem() {
        return getSelectedCharts().values().iterator().next();
    }

    public String getItemName() {
        return getSelectedCharts().keySet().iterator().next();
    }

    public void setActivation(Activation activation) {
        for (Map.Entry<CheckBox, Activation> activationEntry : activations.entrySet()) {
            if (activationEntry.getValue().getName().equals(activation.getName())){
                activations.keySet().forEach(a -> a.setSelected(false));
                activationEntry.getKey().setSelected(true);
                activationEntry.getValue().setScale(activation.getScale());
                return;
            }
        }
    }
}
