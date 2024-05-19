package com.synapse.data.convertation;

import com.synapse.core.samples.Sample;
import com.synapse.core.tools.CoreContext;
import com.synapse.data.dataset.Digiface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvertFromDigifaceBinToSamples {

    public static void main(String[] args) throws InterruptedException {
        String root = "C:\\Users\\User\\Desktop\\Михаил Ханов\\DigiFace";
        File sourceDir = Path.of(root, "images").toFile();
        File resultDir = Path.of(root, "samples").toFile();

        Arrays.stream(resultDir.listFiles()).forEach(File::delete);

        File[] dirs = sourceDir.listFiles();
        dirs = Arrays.stream(dirs)
                .map(file -> Map.entry(Integer.parseInt(file.getName()), file))
                .sorted(Map.Entry.comparingByKey())
                .limit(3)
                .map(Map.Entry::getValue)
                .toArray(File[]::new);

        List<Callable<File>> tasks = getTasks(resultDir, dirs);
        ExecutorService executorService = CoreContext.EXECUTOR_SERVICE;
        executorService.invokeAll(tasks);
        executorService.shutdown();
    }

    private static List<Callable<File>> getTasks(File resultDir, File[] dirs) {
        List<Callable<File>> tasks = new ArrayList<>();
        for (int index = 0; index < dirs.length; index++) {
            for (File file : dirs[index].listFiles()) {
                tasks.add(getTask(resultDir, file, index, dirs.length));
            }
        }
        return tasks;
    }

    private static Callable<File> getTask(File resultDir, File file, int index, int length) {
        return () -> {
            Sample sample = Digiface.convert(file, index, length);
            String name = file.getParentFile().getName() + "_" + file.getName().split("\\.")[0] + ".sample";
            File resultFile = Files.createFile(Path.of(resultDir.getPath(), name)).toFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile))) {
                out.writeObject(sample);
            }
            return resultFile;
        };
    }

}
