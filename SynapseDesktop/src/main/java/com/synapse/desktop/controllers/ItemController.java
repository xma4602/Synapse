package com.synapse.desktop.controllers;

import javafx.fxml.Initializable;

public interface ItemController<T> extends Initializable {

    boolean isValid();
    T getItem();
}
