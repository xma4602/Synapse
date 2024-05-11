package com.synapse.data.learning;

import com.synapse.core.activation.Activation;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
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

class LearningCifar10 {

    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }


    public static void main(String[] args) throws IOException {
        String root = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\программа\\datasets\\cifar-10";
        Path trainingFile = Path.of(root, "samples", "data_batch_1.sample");
        Path testingFile = Path.of(root, "samples", "test_batch.sample");
        Path resultFile = Path.of(root, "experimentation", getNow() + "_result.exp.res");

        FileSampleService service = new FileSampleService();
        service.setTrainingFile(1000, trainingFile.toFile());
        service.setTestingFile(500, testingFile.toFile());
        ListSampleService sampleService = new ListSampleService(service);

        Experimenter experimenter = new ParallelExperimenter();
        experimenter.setActivations(Activation.arrayOf(Activation.getDefault(), 2));
        experimenter.setBatchSizes(1, 5, 10);
        experimenter.setErrorLimits(0.1);
        experimenter.setRates(Rate.getDefault());
        experimenter.setEpochCounts(10);
        experimenter.setSampleServices(sampleService);
        experimenter.setLayerSizes(
                new int[]{32 * 32 * 3, 1000, 10},
                new int[]{32 * 32 * 3, 2000, 10},
                new int[]{32 * 32 * 3, 3000, 10}
        );

        ExperimentResult experimentResult = experimenter.call();
        experimentResult.printReport();

        Files.createFile(resultFile);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile.toFile()))) {
            out.writeObject(experimentResult);
        }

    }
}