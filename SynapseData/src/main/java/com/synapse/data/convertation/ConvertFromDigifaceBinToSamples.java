package com.synapse.data.convertation;

import com.synapse.core.samples.Sample;
import com.synapse.data.dataset.Digiface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;

public class ConvertFromDigifaceBinToSamples {

    public static final int N_THREADS = 50;

    public static void main(String[] args) throws InterruptedException {
        Path mainDir = Path.of(".", "DigiFace");
        File sourceDir = Path.of(mainDir.toString(), "images").toFile();
        File resultDir = Path.of(mainDir.toString(), "samples").toFile();

        Arrays.stream(resultDir.listFiles()).forEach(File::delete);

        File[] dirs = sourceDir.listFiles();
        assert dirs != null;
        dirs = Arrays.stream(dirs)
                .map(file -> Map.entry(Integer.parseInt(file.getName()), file))
                .sorted(Map.Entry.comparingByKey())
                .limit(10)
                .map(Map.Entry::getValue)
                .toArray(File[]::new);

        List<Callable<File>> tasks = getTasks(resultDir, dirs);
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        executorService.invokeAll(tasks);
        executorService.shutdown();
    }

    private static List<Callable<File>> getTasks(File resultDir, File[] dirs) {
        List<Callable<File>> tasks = new ArrayList<>();
        for (int i = 0; i < dirs.length; i++) {
            File[] files = dirs[i].listFiles();
            assert files != null;
            for (File file : files) {
                tasks.add(getTask(resultDir, file, i, dirs.length));
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
