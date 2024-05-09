package com.synapse.desktop.io;

import javafx.stage.FileChooser;
import lombok.Getter;

import java.io.FileFilter;

public enum Extension {
    ACTIVATION("activation"),
    SAMPLE("sample"),
    MATRIX("matrix"),
    NET("network"),
    NET_PAR("net.par"),
    TRAIN_PAR("train.par"),
    TRAIN_RES("train.res"),
    EXP_PAR("exp.par"),
    EXP_RES("exp.res");

    @Getter
    private final String extensionName;
    Extension(String name) {
        this.extensionName = name;
    }

    @Override
    public String toString() {
        return "*." + extensionName;
    }

    public FileFilter getFileFilter() {
        return pathname -> pathname.getName().endsWith(toString());
    }

    public FileChooser.ExtensionFilter getExtensionFilter() {
        return new FileChooser.ExtensionFilter(name(), toString());
    }
}
