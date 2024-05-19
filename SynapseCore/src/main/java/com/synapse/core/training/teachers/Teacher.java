package com.synapse.core.training.teachers;

import com.synapse.core.experimentation.ExperimentParameters;
import com.synapse.core.nets.NetParameters;
import com.synapse.core.samples.SampleService;
import com.synapse.core.tools.Monitored;
import com.synapse.core.training.TrainingParameters;
import com.synapse.core.training.TrainingResult;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;

public abstract class Teacher implements Runnable, Callable<TrainingResult>, Monitored {

    @Setter
    protected String teacherName = "teacher0";

    @Getter
    @Setter
    protected SampleService sampleService;
    @Getter
    @Setter
    protected NetParameters netParameters;
    @Getter
    @Setter
    protected TrainingParameters trainingParameters;
    @Getter
    protected TrainingResult trainingResult;

    public abstract void learn();

    public void setParameters(ExperimentParameters parameters) {
        setNetParameters(parameters.getNetParameters());
        setTrainingParameters(parameters.getTrainingParameters());
        setSampleService(parameters.getSampleService());
    }

    @Override
    public void run() {
        learn();
    }

    @Override
    public TrainingResult call() {
        learn();
        return getTrainingResult();
    }

    @Override
    public String getProcessName() {
        return teacherName;
    }

}
