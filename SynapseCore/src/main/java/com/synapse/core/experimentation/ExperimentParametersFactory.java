package com.synapse.core.experimentation;

import com.synapse.core.activation.Activation;
import com.synapse.core.training.TrainingParameters;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.SampleService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class ExperimentParametersFactory {
    private List<NetParameters> netParams = new ArrayList<>();
    private List<TrainingParameters> trainingParams = new ArrayList<>();
    private List<SampleService> sampleServices;

    public List<ExperimentParameters> getExpParams() {
        List<ExperimentParameters> expParams = new ArrayList<>();
        ExperimentParameters expParam;
        for (var netParam : netParams)
            for (var learningParam : trainingParams)
                for (var sampleService : sampleServices) {
                    expParam = new ExperimentParameters();
                    expParam.setNetParameters(netParam);
                    expParam.setTrainingParameters(learningParam);
                    expParam.setSampleService(sampleService);

                    expParams.add(expParam);
                }

        return expParams;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public void setSampleServices(List<SampleService> sampleServices) {
        this.sampleServices = sampleServices;
    }

    public void setLayerSizes(List<int[]> layerSizes) {
        setNetParams(NetParameters::setLayerSizes, layerSizes);
    }

    public void setActivations(List<Activation[]> activations) {
        setNetParams(NetParameters::setActivations, activations);
    }


    public void setRates(List<Rate> rates) {
        setTrainingParams(TrainingParameters::setRate, rates);
    }

    public void setErrorLimits(List<Double> errorLimits) {
        setTrainingParams(TrainingParameters::setErrorLimit, errorLimits);
    }

    public void setBatchSizes(List<Integer> batchSizes) {
        setTrainingParams(TrainingParameters::setBatchSize, batchSizes);
    }

    public void setMaxEpochs(List<Integer> epochs) {
        setTrainingParams(TrainingParameters::setMaxEpochsCount, epochs);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    private <T> void setNetParams(BiConsumer<NetParameters, T> setter, List<T> values) {
        List<NetParameters> list = new LinkedList<>();
        NetParameters temp;

        if (netParams.isEmpty()) {
            for (var value : values) {
                temp = new NetParameters();
                setter.accept(temp, value);
                list.add(temp);
            }
        } else {
            for (var value : values) {
                for (var netParam : netParams) {
                    temp = netParam.clone();
                    setter.accept(temp, value);
                    list.add(temp);
                }
            }
        }
        netParams = list;
    }

    private <T> void setTrainingParams(BiConsumer<TrainingParameters, T> setter, List<T> values) {
        List<TrainingParameters> list = new LinkedList<>();
        TrainingParameters temp;

        if (trainingParams.isEmpty()) {
            for (var value : values) {
                temp = new TrainingParameters();
                setter.accept(temp, value);
                list.add(temp);
            }

        } else {
            for (var value : values) {
                for (var learningParam : trainingParams) {
                    temp = learningParam.clone();
                    setter.accept(temp, value);
                    list.add(temp);
                }
            }
        }
        trainingParams = list;
    }

    public void setNetParameters(List<NetParameters> netParameters) {
        this.netParams.addAll(netParameters);
    }

    public void setTrainingParameters(List<TrainingParameters> trainingParameters) {
        this.trainingParams.addAll(trainingParameters);
    }
}
