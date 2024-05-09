package com.synapse.desktop.controllers;

import com.synapse.core.training.TrainingParameters;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class TrainingParametersController implements ItemController<TrainingParameters> {

    @FXML
    private Spinner<Integer> batchSizeSpinner;

    @FXML
    private Spinner<Integer> epochSpinner;

    @FXML
    private Spinner<Double> errorLimitSpinner;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        batchSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1, 1));
        epochSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 100, 1));
        errorLimitSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, 0.01, 1e-2));
        fixSpinner(batchSizeSpinner);
        fixSpinner(epochSpinner);
        fixSpinner(errorLimitSpinner);
    }

    <T> void fixSpinner(Spinner<T> spinner) {
        spinner.focusedProperty().addListener((observableValue, aBoolean, t1) ->
                checkValue(spinner));
    }

    <T> void checkValue(Spinner<T> spinner) {
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> factory = spinner.getValueFactory();
        StringConverter<T> converter = factory.getConverter();
        try {
            T value = converter.fromString(text);
            factory.setValue(value);
        } catch (NumberFormatException e) {
            spinner.getEditor().setText(converter.toString(factory.getValue()));
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public TrainingParameters getItem() {
        var p = new TrainingParameters();
        p.setBatchSize(batchSizeSpinner.getValue());
        p.setMaxEpochsCount(epochSpinner.getValue());
        p.setErrorLimit(errorLimitSpinner.getValue());
        return p;
    }

    public void setTrainingParameters(TrainingParameters trainingParameters) {
        batchSizeSpinner.getValueFactory().setValue(trainingParameters.getBatchSize());
        epochSpinner.getValueFactory().setValue(trainingParameters.getMaxEpochsCount());
        errorLimitSpinner.getValueFactory().setValue(trainingParameters.getErrorLimit());
    }
}
