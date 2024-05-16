package com.synapse.data.learning;

import com.synapse.core.activation.Activation;
import com.synapse.core.activation.ActivationLog;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
import com.synapse.core.rates.ConstantRate;
import com.synapse.core.samples.FileSampleService;
import com.synapse.core.samples.InMemorySampleService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LearningCifar10 {

    public static final int CIFAR10_INPUT = 32 * 32 * 3;
    public static final int CIFAR10_OUTPUT = 10;

    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }


    public static void main(String[] args) throws IOException {
        String root = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\программа\\datasets\\cifar-10";
        Path trainingFile = Path.of(root, "samples", "data_batch_1.sample");
        Path testingFile = Path.of(root, "samples", "test_batch.sample");

        InMemorySampleService sampleService = new InMemorySampleService(new FileSampleService(
                List.of(trainingFile.toFile()), 1000,
                List.of(testingFile.toFile()), 500
        ));

        Experimenter experimenter = new ParallelExperimenter();
        experimenter.setBatchSizes(1);
        experimenter.setErrorLimits(0.1);
        experimenter.setActivations(
                Activation.arrayOf(new ActivationLog(0.2), 2),
                Activation.arrayOf(new ActivationLog(1.0), 2),
                Activation.arrayOf(new ActivationLog(4.0), 2)
        );
        experimenter.setRates(
                new ConstantRate(1)
        );
        experimenter.setEpochCounts(10);
        experimenter.setSampleServices(sampleService);
        experimenter.setLayerSizes(new int[]{CIFAR10_INPUT, 5000, CIFAR10_OUTPUT});

        ExperimentResult experimentResult = experimenter.call();
        experimentResult.printReport();

        Path resultFile = Path.of(root, "experimentation", getNow() + "_result.exp.res");
        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }

    }
}