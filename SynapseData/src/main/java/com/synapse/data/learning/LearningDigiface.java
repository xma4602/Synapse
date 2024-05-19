package com.synapse.data.learning;

import com.synapse.core.activation.ActivationLog;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.SerialExperimenter;
import com.synapse.core.rates.ConstantRate;
import com.synapse.core.samples.FileSampleService;
import com.synapse.core.samples.InMemorySampleService;
import com.synapse.core.samples.SampleService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LearningDigiface {

    public static final int DIGIFACE_INPUT = 112 * 112 * 3;
    public static final int DIGIFACE_OUTPUT = 3;

    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }


    public static void main(String[] args) throws IOException {
        learn();
    }

    public static ExperimentResult learn() throws IOException {
        String root = "C:\\Users\\User\\Desktop\\Михаил Ханов\\DigiFace";
        File[] samples = Path.of(root, "samples").toFile().listFiles();

        Experimenter experimenter = new SerialExperimenter();

        experimenter.setSampleServices(
                new InMemorySampleService(new FileSampleService(SampleService.makeSampling(0.75, samples)))
        );
        experimenter.setLayerSizes(
                new int[]{DIGIFACE_INPUT, 10000, DIGIFACE_OUTPUT}
        );
        experimenter.setActivations(
                new ActivationLog(0.4)
        );
        experimenter.setRates(
                new ConstantRate(2.0)
        );
        experimenter.setBatchSizes(1);
        experimenter.setErrorLimits(0.1);
        experimenter.setEpochCounts(10);

        ExperimentResult experimentResult = experimenter.call();
        experimentResult.printReport();

        Path resultFile = Path.of(root, "experimentation", getNow() + "_result.exp.res");
        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }
        System.out.println("File saved: " + resultFile);
        return experimentResult;
    }
}