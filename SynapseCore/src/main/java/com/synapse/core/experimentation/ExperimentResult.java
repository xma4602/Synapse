package com.synapse.core.experimentation;

import com.synapse.core.tools.Reportable;
import com.synapse.core.training.TrainingResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Duration;
import java.util.*;

@NoArgsConstructor
public class ExperimentResult implements Externalizable, Reportable {

    private SortedMap<Integer, Map.Entry<ExperimentParameters, TrainingResult>> experiences = new TreeMap<>();
    //    private ExperimentStatistics statistics = null;
    @Getter
    @Setter
    private Duration duration;

    @Override
    public List<String> getReport() {
        List<String> report = new ArrayList<>();
        report.add("ExperimentResult:\n");
        report.add("\tduration=%dч. %dм. %dс.\n".formatted(duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
        for (var resultEntry : experiences.entrySet()) {
            Integer index = resultEntry.getKey();
            var experiment = resultEntry.getValue();
            report.add("\tExperiment %d:\n".formatted(index + 1));
            experiment.getKey().getReport().forEach(r -> report.add("\t\t" + r));
            experiment.getValue().getReport().forEach(r -> report.add("\t\t" + r));
        }
        return report;
    }

    public void addExperience(ExperimentParameters parameters, TrainingResult result) {
        experiences.put(experiences.size(), Map.entry(parameters, result));
    }
//
//    public ExperimentStatistics getStatistics() {
//        if (statistics == null) statistics =
//                new ExperimentStatistics(results.values().stream()
//                        .map(Collection::stream)
//                        .reduce(Stream.empty(), Stream::concat)
//                        .collect(Collectors.toList()));
//        return statistics;
//    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(duration.toNanos());
        out.writeInt(experiences.size());
        for (var entry : experiences.entrySet()) {
            Map.Entry<ExperimentParameters, TrainingResult> value = entry.getValue();
            out.writeObject(value.getKey());
            out.writeObject(value.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        duration = Duration.ofNanos(in.readLong());
        int length = in.readInt();
        for (int i = 0; i < length; i++) {
            ExperimentParameters experimentParameters = (ExperimentParameters) in.readObject();
            TrainingResult trainingResults = (TrainingResult) in.readObject();
            experiences.put(i, Map.entry(experimentParameters, trainingResults));
        }
    }

//
//    public static class ExperimentStatistics {
//        Net bestNet;
//
//        public ExperimentStatistics(Collection<TrainingResult> results) {
//            int count = 0;
//            results.stream().map(TrainingResult::getTrainingErrors).re
//        }
//
//    }
}
