package com.synapse.desktop.io;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class FileSaver extends FileService {
    public static final Path DEFAULT_DIR = Path.of(System.getProperty("user.home") + "/Desktop/SynapseTemp");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private final Window window;

    public static void defaultSave(Extension extension, Object object) {
        try {
            if (Files.notExists(DEFAULT_DIR)) Files.createDirectory(DEFAULT_DIR);
            Path path = Path.of(DEFAULT_DIR.toString(), LocalDateTime.now().format(DATE_TIME_FORMATTER) + "." + extension.getExtensionName());
            Files.createFile(path);
            save(object, path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File selectFilePath(Extension extension) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранение файла " + extension.name());
        chooser.setInitialDirectory(initial);
        chooser.getExtensionFilters().add(extension.getExtensionFilter());

        File result = chooser.showSaveDialog(window);
        if (result != null) {
            initial = result.getParentFile();
        }
        return result;
    }

//    public List<File> createAnyFile(Extension extension, int count) throws IOException {
//        FileChooser chooser = new FileChooser();
//        chooser.setTitle("Сохранение нескольких файлов " + extension.name());
//        chooser.setInitialDirectory(initial);
//        chooser.getExtensionFilters().add(extension.getExtensionFilter());
//
//        File startFile = chooser.showSaveDialog(window);
//        List<File> result = Collections.emptyList();
//        if (startFile != null) {
//            initial = startFile.getParentFile();
//            result = createFiles(startFile, count);
//        }
//
//        return result;
//    }
//
//    public List<File> createFiles(File startFile, int count) throws IOException {
//        List<File> files = new LinkedList<>();
//        FileName fileName = FileName.from(startFile.getName());
//
//        File file;
//        for (int i = 0; i < count; i++) {
//            file = new File(startFile.getParent(), fileName.name + (fileName.number + i));
//            file.createNewFile();
//            files.add(file);
//        }
//
//        return files;
//    }

    public File selectPathAndSaveObject(Extension extension, Object object) {
        File file = selectFilePath(extension);
        save(object, file);
        return file;
    }

    private static void save(Object object, File file) {
        if (file != null) {
            try (var out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(object);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private record FileName(String name, int number) {
        public static FileName from(String s) {
            String name = "file";
            int number = 1;
            Pattern pattern = Pattern.compile("(\\D+)(\\d*)\\.\\w*");
            Matcher matcher = pattern.matcher(s);

            if (matcher.find()) {
                name = matcher.group(1);
                number = Integer.parseInt(matcher.group(2));
            }
            return new FileName(name, number);
        }
    }
}
