package com.synapse.desktop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class NetStructureController implements ItemController<int[]> {

    @FXML
    private Label inputLabel;
    @FXML
    private Label midLabel;
    @FXML
    private Label outputLabel;
    @FXML
    private TextField inputLayerField;
    @FXML
    private TextField midLayerField;
    @FXML
    private TextField outputLayerField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputLabel.setText("");
        midLabel.setText("");
        outputLabel.setText("");

        inputLayerField.textProperty().addListener((observable, oldValue, newValue) -> inputLabel.setText(
                parseLayer(newValue) != null ?
                        "" : "Значение должно быть натуральным числом\n(возможно в формате перемножения натуральных чисел)"
        ));
        outputLayerField.textProperty().addListener((observable, oldValue, newValue) -> outputLabel.setText(
                parseLayer(newValue) != null ?
                        "" : "Значение должно быть натуральным числом\n(возможно в формате перемножения натуральных чисел)"
        ));
        midLayerField.textProperty().addListener((observable, oldValue, newValue) -> midLabel.setText(
                parseLayers(newValue) != null ?
                        "" : "Значения должны быть натуральными числами, записанными через пробел"
        ));
    }

    private Integer parseLayer(String text) {
        try {
            return Arrays.stream(text.split("\\*"))
                    .map(String::trim)
                    .map(Integer::parseUnsignedInt)
                    .reduce(1, (x, y) -> x * y);
        } catch (Exception e) {
            return null;
        }
    }

    private int[] parseLayers(String text) {
        try {
            return Arrays.stream(text.split(" "))
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseUnsignedInt)
                    .toArray();
        } catch (Exception e) {
            return null;
        }
    }


    public int getInputLayerSize() {
        return parseLayer(inputLayerField.getText());
    }

    public int getOutputLayerSize() {
        return parseLayer(outputLayerField.getText());
    }

    @Override
    public boolean isValid() {
        return parseLayer(inputLayerField.getText()) != null
                && parseLayer(outputLayerField.getText()) != null
                && parseLayers(midLayerField.getText()) != null;
    }

    @Override
    public int[] getItem() {
        int input = parseLayer(inputLayerField.getText());
        int[] mid = parseLayers(midLayerField.getText());
        int output = parseLayer(outputLayerField.getText());

        int[] layerSizes = new int[mid.length + 2];

        layerSizes[0] = input;
        System.arraycopy(mid, 0, layerSizes, 1, mid.length);
        layerSizes[layerSizes.length - 1] = output;

        return layerSizes;
    }

    public void setLayers(int... item) {
        inputLayerField.setText(String.valueOf(item[0]));
        outputLayerField.setText(String.valueOf(item[item.length - 1]));
        if (item.length > 2) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < item.length - 1; i++) {
                builder.append(item[i]).append(" ");
            }
            midLayerField.setText(builder.toString());
        }
    }


}
