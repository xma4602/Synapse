package com.synapse.data.convertation;

import com.synapse.core.samples.Sample;
import com.synapse.data.dataset.Cifar10;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvertFromCifar10BinToSamples {

    public static final int N_THREADS = 50;
    static String root = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\программа\\datasets\\cifar-10";
    static Path bin = Path.of(root, "bin");
    static Path samples = Path.of(root, "samples");

    public static void main(String[] args) throws InterruptedException {
        File[] files = bin.toFile().listFiles();
        assert files != null;

        List<Callable<File>> tasks = Arrays.stream(files)
                .map(file -> (Callable<File>) () -> convertFromBinToSample(file))
                .toList();
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        executorService.invokeAll(tasks);
        executorService.shutdown();
    }

    private static File convertFromBinToSample(File binFile) {
        try {
            File sampleFile = getSampleFile(binFile);
            sampleFile.createNewFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(sampleFile))) {
                int i = 1;
                for (Sample sample : Cifar10.readSamplesFromBinFile(binFile)) {
                    out.writeObject(sample);
                    System.out.printf("FILE: %17s | SAMPLE: %04d%n", binFile.getName(), i++);
                }
            }
            return sampleFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static File getSampleFile(File file) throws IOException {
        String name = file.getName().split("\\.")[0];
        Path path = Path.of(samples.toString(), name + ".sample");
        if (Files.notExists(path)) Files.createFile(path);
        return path.toFile();
    }
}
