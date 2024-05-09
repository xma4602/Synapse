package com.synapse.desktop.controllers;

import com.synapse.core.samples.ListSampleService;
import com.synapse.core.samples.Sample;
import com.synapse.core.samples.SampleService;
import com.synapse.desktop.io.Extension;
import com.synapse.desktop.io.FileLoader;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SampleController implements ItemController<SampleService> {
    @FXML
    private Label countLabel;
    @FXML
    private Label testingLabel;
    @FXML
    private Label trainingLabel;

    @FXML
    private Spinner<Double> trainingSpinner;
    @FXML
    private Spinner<Double> testingSpinner;

    @FXML
    private TableView<SampleView> table;
    TableColumn<SampleView, Integer> sourceColumn;
    TableColumn<SampleView, Integer> targetColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();

        initSpinner(trainingSpinner, 0.7);
        initSpinner(testingSpinner, 0.3);
        trainingSpinner.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    testingSpinner.getValueFactory().setValue(1 - newValue);
                    updateSpinnerLabels();
                }
        );
        testingSpinner.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    trainingSpinner.getValueFactory().setValue(1 - newValue);
                    updateSpinnerLabels();
                }
        );

        updateLabels();
    }

    private void initTable() {
        TableColumn<SampleView, Integer> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(col.getValue()) + 1));

        numberColumn.setMaxWidth(30);
        numberColumn.setMinWidth(30);

        TableColumn<SampleView, String> nameColumn = new TableColumn<>("Файл");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        sourceColumn = new TableColumn<>("Входной вектор");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourceSize"));

        targetColumn = new TableColumn<>("Выходной вектор");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("targetSize"));

        table.getColumns().addAll(numberColumn, nameColumn, sourceColumn, targetColumn);
    }

    private void initSpinner(Spinner<Double> spinner, double value) {
        var factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(
                Double.MIN_VALUE, 1.0, value, 0.1);
        spinner.setValueFactory(factory);
    }

    private void updateLabels() {
        updateTableLabel();
        updateSpinnerLabels();
    }

    private void updateTableLabel() {
        int size = table.getItems().size();

        if (!isValidColumn(sourceColumn)) {
            countLabel.setText("Размеры входных векторов неодинаковы");
            countLabel.setTextFill(Color.RED);
        } else if (!isValidColumn(targetColumn)) {
            countLabel.setText("Размеры выходных векторов неодинаковы");
            countLabel.setTextFill(Color.RED);
        } else {
            countLabel.setText("Обучающая выборка: %d элементов".formatted(size));
            countLabel.setTextFill(Color.BLACK);
        }
    }

    boolean isValidColumn(TableColumn<SampleView, Integer> column) {
        if (table.getItems().isEmpty()) return true;

        int columnCellData = column.getCellData(0);
        for (int i = 1; i < table.getItems().size(); i++) {
            if (columnCellData != column.getCellData(i)) return false;
        }
        return true;
    }

    private void updateSpinnerLabels() {
        if (isValidSpinnersValues()) {
            long trainingSize = Math.round(table.getItems().size() * trainingSpinner.getValue());
            long testingSize = table.getItems().size() - trainingSize;
            trainingLabel.setText("Количество элементов: " + trainingSize);
            testingLabel.setText("Количество элементов: " + testingSize);
            trainingLabel.setTextFill(Color.BLACK);
            testingLabel.setTextFill(Color.BLACK);
        } else {
            trainingLabel.setText("Заданы неверные параметры");
            testingLabel.setText("Заданы неверные параметры");
            trainingLabel.setTextFill(Color.RED);
            testingLabel.setTextFill(Color.RED);
        }
    }

    private boolean isValidSpinnersValues() {
        return trainingSpinner.getValue() > 1e-6 && testingSpinner.getValue() > 1e-6;
    }

    @FXML
    private void addSamples() {
        List<File> files = new FileLoader(table.getScene().getWindow()).loadAnyFiles(Extension.SAMPLE);
        for (File file : files) {
            try (var in = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    Sample sample = (Sample) in.readObject();
                    table.getItems().add(new SampleView(file, sample));
                }
            } catch (EOFException ignored) {
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        updateLabels();
    }

    @FXML
    private void delete() {
        table.getItems().removeAll(table.getSelectionModel().getSelectedItems());
        updateLabels();
    }

    @FXML
    private void clear() {
        table.getItems().clear();
        updateLabels();
    }

    @Override
    public boolean isValid() {
        return !table.getItems().isEmpty() &&
                isValidColumn(sourceColumn) &&
                isValidColumn(targetColumn) &&
                isValidSpinnersValues();
    }

    @Override
    public SampleService getItem() {
        List<Sample> list = table.getItems().stream().map(SampleView::getSample).toList();
        return new ListSampleService(trainingSpinner.getValue(), list);
    }

    public int getInputLayerSize() {
        return table.getItems().get(0).getSourceSize();
    }

    public int getOutputLayerSize() {
        return table.getItems().get(0).getTargetSize();
    }

    @Getter
    public static class SampleView { //обязательно public, чтобы работала таблица
        private String name;
        private Integer sourceSize;
        private Integer targetSize;
        private Sample sample;

        public SampleView(File file, Sample sample) {
            name = file.getName().substring(0, file.getName().lastIndexOf('.'));
            sourceSize = sample.getSourceSize();
            targetSize = sample.getTargetSize();
            this.sample = sample;
        }

        @Override
        public boolean equals(Object obj) { //необходимо для правильной индексации в таблице
            return this == obj;
        }
    }
}
