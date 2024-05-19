package com.synapse.data.learning;

import com.synapse.core.activation.Activation;
import com.synapse.core.activation.ActivationLog;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
import com.synapse.core.experimentation.SerialExperimenter;
import com.synapse.core.rates.ConstantRate;
import com.synapse.core.samples.InMemorySampleService;
import com.synapse.core.samples.Sample;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LearningIris {

    public static final int IRIS_INPUT = 4;
    public static final int IRIS_OUTPUT = 3;


    public static void main(String[] args) throws IOException {
        learn();
    }

    public static ExperimentResult learn() throws IOException {
        String root = "C:\\Users\\User\\Desktop\\Михаил Ханов\\iris";
        Path dataFile = Path.of(root, "samples", "data.sample");


        Experimenter experimenter = new ParallelExperimenter();
        InMemorySampleService sampleService = new InMemorySampleService(0.75, readSamples(dataFile));
        experimenter.setActivations(
                new ActivationLog(0.1),
                new ActivationLog(0.2),
                new ActivationLog(0.3)
        );
        experimenter.setBatchSizes(1);
        experimenter.setErrorLimits(0.01);
        experimenter.setRates(
                new ConstantRate(0.5),
                new ConstantRate(1.0),
                new ConstantRate(1.5)
        );
        experimenter.setEpochCounts(100);
        experimenter.setSampleServices(sampleService);
        experimenter.setLayerSizes(new int[]{IRIS_INPUT, 100, IRIS_OUTPUT});

        ExperimentResult experimentResult = experimenter.call();
        experimentResult.printReport();

        Path resultFile = Path.of(root, "experimentation", getNow() + "_iris_result.exp.res");
        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }
        return experimentResult;
    }

    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }

    private static List<Sample> readSamples(Path dataFile) throws IOException {
        List<Sample> samples = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile.toFile()))) {
            while (true) {
                samples.add((Sample) in.readObject());
            }
        } catch (EOFException ignored) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return samples;
    }
}