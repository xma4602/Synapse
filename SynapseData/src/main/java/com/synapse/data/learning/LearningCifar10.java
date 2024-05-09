package com.synapse.data.learning;

import com.synapse.core.activation.Activation;
import com.synapse.core.experimentation.ExperimentResult;
import com.synapse.core.experimentation.Experimenter;
import com.synapse.core.experimentation.ParallelExperimenter;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.FileSampleService;
import com.synapse.core.training.TrainingParameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class LearningCifar10 {

    static String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }

    public static void main(String[] args) throws IOException {
        File trainingFile = Path.of("cifar-10/samples", "data_batch_1.sample").toFile();
        File testingFile = Path.of("cifar-10/samples", "test_batch.sample").toFile();
        File resultFile = Path.of("cifar-10/experimentation", getNow() + "_result.exp.res").toFile();

        FileSampleService sampleService = new FileSampleService();
        sampleService.setTrainingFile(5000, trainingFile);
        sampleService.setTestingFile(1000, testingFile);

        NetParameters netParameters = new NetParameters();
        netParameters.setLayerSizes(new int[]{32 * 32 * 3, 100, 10});
        netParameters.setActivations(Activation.arrayOf(Activation.getDefault(), 2));

        TrainingParameters trainingParameters = new TrainingParameters();
        trainingParameters.setBatchSize(1);
        trainingParameters.setErrorLimit(0.1);
        trainingParameters.setRate(Rate.getDefault());
        trainingParameters.setMaxEpochsCount(10);

        Experimenter experimenter = new ParallelExperimenter();
        experimenter.setNetParameters(netParameters);
        experimenter.setTrainingParameters(trainingParameters);
        experimenter.setSampleServices(sampleService);

        ExperimentResult experimentResult = experimenter.call();
        experimentResult.printReport();

        resultFile.createNewFile();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile))) {
            out.writeObject(experimentResult);
        }

    }
}