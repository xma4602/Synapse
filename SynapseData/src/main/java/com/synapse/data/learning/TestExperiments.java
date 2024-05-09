package com.synapse.data.learning;

import com.synapse.core.activation.*;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.rates.ConstantRate;
import com.synapse.core.rates.ExponentRate;
import com.synapse.core.rates.LinearRate;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.FileSampleService;
import com.synapse.core.samples.ListSampleService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class TestExperiments {


    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }

    public static void main(String[] args) throws IOException {
        String root = "C:\\Users\\User\\Desktop\\Михаил Ханов\\cifar-10";
        Path trainingFile = Path.of(root, "samples", "data_batch_1.sample");
        Path testingFile = Path.of(root, "samples", "test_batch.sample");
        Path resultFile = Path.of(root, "experimentation", getNow() + "_result.exp.res");

        FileSampleService service = new FileSampleService();
        service.setTrainingFile(500, trainingFile.toFile());
        service.setTestingFile(100, testingFile.toFile());
        ListSampleService sampleService = new ListSampleService(service);

        Experimenter experimenter = new ParallelExperimenter();
        experimenter.setSampleServices(sampleService);
        experimenter.setRates(new Rate[]{
//                new ConstantRate(0.2), new ConstantRate(0.5), new ConstantRate(0.7),
//                new LinearRate(0.2), new LinearRate(0.5), new LinearRate(0.7),
                new ExponentRate(1.0, 0.2), new ExponentRate(1.0, 0.5), new ExponentRate(1.0, 0.7),
        });
        experimenter.setErrorLimits(0.1);
        experimenter.setEpochCounts(10);
        experimenter.setBatchSizes(1);
        experimenter.setActivations(Activation.arrayOf(Activation.getDefault(), 3));
        experimenter.setLayerSizes(new int[]{3072, 5000, 5000, 10});
        ExperimentResult experimentResult = experimenter.call();
        experimentResult.getReport().forEach(System.out::print);

        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }
    }
}