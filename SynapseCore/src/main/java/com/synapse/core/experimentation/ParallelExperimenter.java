package com.synapse.core.experimentation;

import com.synapse.core.rates.Rate;
import com.synapse.core.tools.CoreContext;
import com.synapse.core.tools.Monitored;
import com.synapse.core.tools.Timing;
import com.synapse.core.training.TrainingResult;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@NoArgsConstructor
public class ParallelExperimenter extends Experimenter {

    protected final ExecutorService executor = CoreContext.EXECUTOR_SERVICE;
    protected List<Experiment> experiments = new ArrayList<>();

    public static ParallelExperimenter getDefault() {
        ParallelExperimenter experimenter = new ParallelExperimenter();
        experimenter.setEpochCounts(10);
        experimenter.setErrorLimits(1e-3);
        experimenter.setRates(Rate.getDefault());
        return experimenter;
    }

    @Override
    protected ExperimentResult performExperiments(List<ExperimentParameters> parameters) {
        try {
            for (int i = 0; i < parameters.size(); i++) {
                Experiment experiment = new Experiment(parameters.get(i));
                experiment.setExperimentName(experimenterName + "-" + "experiment" + i);
                experiments.add(experiment);
            }
            Timing timing = Timing.startTiming();

            List<Future<TrainingResult>> futures = executor.invokeAll(experiments);
            executor.shutdown();

            ExperimentResult result = new ExperimentResult();
            for (int i = 0; i < futures.size(); i++) {
                result.addExperience(parameters.get(i), futures.get(i).get());
            }

            result.setDuration(timing.stopTiming());
            return result;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
