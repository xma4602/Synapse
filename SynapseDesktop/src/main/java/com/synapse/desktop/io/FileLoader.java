package com.synapse.desktop.io;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class FileLoader extends FileService {

    private final Window window;

    public <T> T loadOneObject(Extension extension) throws IOException, ClassNotFoundException {
        File file = loadOneFile(extension);
        if (file != null) {
            try (var in = new ObjectInputStream(new FileInputStream(file))) {
                return (T) in.readObject();
            }
        }
        return null;
    }

    public File loadOneFile(Extension extension) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выбор файла " + extension);
        chooser.setInitialDirectory(initial);
        chooser.getExtensionFilters().add(extension.getExtensionFilter());

        File result = chooser.showOpenDialog(window);
        if (result != null) {
            initial = result.getParentFile();
        }
        return result;
    }

    public List<File> loadAnyFiles(Extension extension) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выбор нескольких файлов " + extension.name());
        chooser.setInitialDirectory(initial);
        chooser.getExtensionFilters().add(extension.getExtensionFilter());

        List<File> result = chooser.showOpenMultipleDialog(window);
        if (result == null) {
            result = new ArrayList<>(0);
        } else {
            initial = result.get(0).getParentFile();
        }
        return result;
    }

    public List<File> loadAllFiles(Extension extension) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выбор всех файлов " + extension.name() + " в дериктории");
        chooser.setInitialDirectory(initial);

        File dir = chooser.showDialog(window);
        List<File> result = new LinkedList<>();
        if (dir != null) {
            initial = dir;
            result.addAll(Arrays.asList(dir.listFiles(extension.getFileFilter())));
        }
        return result;
    }

}
