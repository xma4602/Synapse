package com.synapse.data.convertation;

import com.synapse.core.samples.Sample;
import com.synapse.data.dataset.Cifar10;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvertFromCifar10BinToSamples {

    public static final int N_THREADS = 50;

    public static void main(String[] args) throws InterruptedException {
        File[] files = Path.of(".","cifar-10", "bin").toFile().listFiles();
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
                for (Sample sample : Cifar10.readBinFile(binFile)) {
                    out.writeObject(sample);
                    System.out.printf("FILE: %17s | SAMPLE: %04d%n", binFile.getName(), i++);
                }
            }
            return sampleFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private static File getSampleFile(File file) {
        String name = file.getName().split("\\.")[0];
        Path path = Path.of("cifar-10", "samples", name + ".sample");
        return new File(path.toUri());
    }
}
