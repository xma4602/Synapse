package com.synapse.data.learning;

import com.synapse.core.activation.Activation;
import com.synapse.core.activation.ActivationAtan;
import com.synapse.core.activation.ActivationLog;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.FileSampleService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class TestActivations {


    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }

    public static void main(String[] args) throws IOException {
        String root = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\программа\\datasets\\cifar-10";
        Path trainingFile = Path.of(root, "samples", "data_batch_1.sample");
        Path testingFile = Path.of(root, "samples", "test_batch.sample");
        Path resultFile = Path.of(root, "experimentation", getNow() + "_result.exp.res");

        FileSampleService sampleService = new FileSampleService(
                List.of(trainingFile.toFile()), 500,
                List.of(testingFile.toFile()), 250
        );

        Experimenter experimenter = new ParallelExperimenter();
        experimenter.setSampleServices(sampleService);
        experimenter.setRates(Rate.getDefault());
        experimenter.setErrorLimits(0.1);
        experimenter.setEpochCounts(10);
        experimenter.setBatchSizes(1);
        experimenter.setLayerSizes(new int[]{3072, 5000, 5000, 10});
        experimenter.setActivations(
                Activation.arrayOf(new ActivationLog(0.1), 3),
                Activation.arrayOf(new ActivationLog(0.2), 3),
                Activation.arrayOf(new ActivationLog(0.5), 3),
                Activation.arrayOf(new ActivationAtan(0.1), 3),
                Activation.arrayOf(new ActivationAtan(0.2), 3),
                Activation.arrayOf(new ActivationAtan(0.5), 3)
//                Activation.arrayOf(new ActivationTanh(0.2), 3),
//                Activation.arrayOf(new ActivationTanh(0.5), 3),
//                Activation.arrayOf(new ActivationTanh(0.7), 3),
//                Activation.arrayOf(new ActivationSin(0.2), 3),
//                Activation.arrayOf(new ActivationSin(0.5), 3),
//                Activation.arrayOf(new ActivationSin(0.7), 3)
        );


        ExperimentResult experimentResult = experimenter.call();
        experimentResult.getReport().forEach(System.out::print);

        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }
    }
}