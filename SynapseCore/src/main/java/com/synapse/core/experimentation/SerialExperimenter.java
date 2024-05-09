package com.synapse.core.experimentation;

import com.synapse.core.tools.Monitored;
import com.synapse.core.tools.Timing;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class SerialExperimenter extends Experimenter {
    List<Experiment> experiments = new ArrayList<>();

    @Override
    protected ExperimentResult performExperiments(List<ExperimentParameters> parameters) {

        for (int i = 0; i < parameters.size(); i++) {
            Experiment experiment = new Experiment(parameters.get(i));
            experiment.setExperimentName(experimenterName + "-" + "experiment" + i);
            experiments.add(experiment);
        }

        ExperimentResult result = new ExperimentResult();
        Timing timing = Timing.startTiming();

        for (int i = 0; i < experiments.size(); i++) {
            result.addExperience(parameters.get(i), experiments.get(i).call());
        }

        result.setDuration(timing.stopTiming());
        return result;
    }

    @Override
    public double getProgress() {
        return experiments.stream().mapToDouble(Experiment::getProgress).sum() / experiments.size();
    }

    @Override
    public Iterable<? extends Monitored> getProcessComponents() {
        return experiments;
    }


}
