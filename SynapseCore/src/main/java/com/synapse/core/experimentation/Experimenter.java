package com.synapse.core.experimentation;

import com.synapse.core.activation.Activation;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.SampleService;
import com.synapse.core.tools.Monitored;
import com.synapse.core.training.TrainingParameters;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class Experimenter implements Callable<ExperimentResult>, Monitored {

    @Getter
    private List<Integer> epochCounts;
    @Getter
    private List<Integer> batchSizes;
    @Getter
    private List<Double> errorLimits;
    @Getter
    private List<int[]> layerSizes;
    @Getter
    private List<Activation[]> activations;
    @Getter
    private List<Rate> rates;
    @Getter
    private List<NetParameters> netParameters;
    @Getter
    private List<TrainingParameters> trainingParameters;
    @Getter
    private List<SampleService> sampleServices;

    @Getter
    private Duration duration;
    protected String experimenterName = "experimenter0";

    protected abstract ExperimentResult performExperiments(List<ExperimentParameters> experimentParameters);

    @Override
    public ExperimentResult call() {
        if (sampleServices.isEmpty()) throw new IllegalStateException("SampleService is not set");
        if (netParameters == null) {
            if (layerSizes == null || layerSizes.isEmpty())
                throw new IllegalStateException("LayerSizes is not set");
            if (activations == null || activations.isEmpty())
                throw new IllegalStateException("Activations is not set");
        }
        if (trainingParameters == null) {
            if (epochCounts == null || epochCounts.isEmpty())
                throw new IllegalStateException("EpochCounts is not set");
            if (errorLimits == null || errorLimits.isEmpty())
                throw new IllegalStateException("ErrorLimits is not set");
            if (rates == null || rates.isEmpty()) throw new IllegalStateException("Rates is not set");
            if (batchSizes == null || batchSizes.isEmpty())
                throw new IllegalStateException("BatchSizes is not set");
        }

        List<ExperimentParameters> parameters = makeExperimentParameters();

        return performExperiments(parameters);
    }

    private List<ExperimentParameters> makeExperimentParameters() {
        var factory = new ExperimentParametersFactory();

        if (netParameters != null)
            factory.setNetParameters(netParameters);
        else {
            factory.setActivations(activations);
            factory.setLayerSizes(layerSizes);
        }
        if (trainingParameters != null)
            factory.setTrainingParameters(trainingParameters);
        else {
            factory.setMaxEpochs(epochCounts);
            factory.setRates(rates);
            factory.setErrorLimits(errorLimits);
            factory.setBatchSizes(batchSizes);
        }
        factory.setSampleServices(sampleServices);

        return factory.getExpParams();
    }

    @Override
    public String getProcessName() {
        return experimenterName;
    }

    public void setSampleServices(SampleService... sampleServices) {
        this.sampleServices = List.of(sampleServices);
    }

    public void setEpochCounts(Integer... epochCounts) {
        this.epochCounts = List.of(epochCounts);
    }

    public void setBatchSizes(Integer... batchSizes) {
        this.batchSizes = List.of(batchSizes);
    }

    public void setErrorLimits(Double... errorLimits) {
        this.errorLimits = List.of(errorLimits);
    }

    public void setLayerSizes(int[]... layerSizes) {
        this.layerSizes = List.of(layerSizes);
    }

    public void setActivations(Activation[]... activations) {
        this.activations = List.of(activations);
    }

    public void setRates(Rate... rates) {
        this.rates = List.of(rates);
    }

    public void setNetParameters(NetParameters... netParameters) {
        this.netParameters = List.of(netParameters);
    }

    public void setTrainingParameters(TrainingParameters... trainingParameters) {
        this.trainingParameters = List.of(trainingParameters);
    }
}
