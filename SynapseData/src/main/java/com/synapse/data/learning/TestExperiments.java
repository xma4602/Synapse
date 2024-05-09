package com.synapse.data.learning;

import com.synapse.core.activation.Activation;
import com.synapse.core.activation.ActivationLogistic;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
import com.synapse.core.nets.NetParameters;
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
        Path trainingFile = Path.of("cifar-10/samples", "data_batch_1.sample");
        Path testingFile = Path.of("cifar-10/samples", "test_batch.sample");
        Path resultFile = Path.of("cifar-10/experimentation", getNow() + "_result.exp.res");

        FileSampleService service = new FileSampleService();
        service.setTrainingFile(1000, trainingFile.toFile());
        service.setTestingFile(500, testingFile.toFile());
        ListSampleService sampleService = new ListSampleService(service);

        Experimenter experimenter = new ParallelExperimenter();
        experimenter.setSampleServices(sampleService);
        experimenter.setRates(Rate.getDefault());
        experimenter.setErrorLimits(0.1);
        experimenter.setEpochCounts(10);
        experimenter.setBatchSizes(1);

        NetParameters[] netParameters = new NetParameters[]{
                new NetParameters(new int[]{3072, 5000, 5000, 10}, Activation.arrayOf(new ActivationLogistic(0.1), 3)),
                new NetParameters(new int[]{3072, 5000, 5000, 10}, Activation.arrayOf(new ActivationLogistic(0.2), 3)),
                new NetParameters(new int[]{3072, 5000, 5000, 10}, Activation.arrayOf(new ActivationLogistic(0.3), 3)),
                new NetParameters(new int[]{3072, 5000, 5000, 10}, Activation.arrayOf(new ActivationLogistic(0.4), 3)),
                new NetParameters(new int[]{3072, 5000, 5000, 10}, Activation.arrayOf(new ActivationLogistic(0.5), 3)),
        };
        experimenter.setNetParameters(netParameters);
        ExperimentResult experimentResult = experimenter.call();
        experimentResult.getReport().forEach(System.out::print);

        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }
    }
}