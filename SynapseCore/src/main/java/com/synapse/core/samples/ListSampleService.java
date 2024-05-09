package com.synapse.core.samples;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ListSampleService implements SampleService {

    @Getter
    private List<Sample> training = new ArrayList<>();
    @Getter
    private List<Sample> testing = new ArrayList<>();

    public ListSampleService(SampleService sampleService) {
        this(sampleService.getTrainingSampling(), sampleService.getTestingSampling());
    }

    public ListSampleService(Iterable<Sample> trainingSamples, Iterable<Sample> testingSamples) {
        trainingSamples.forEach(training::add);
        testingSamples.forEach(testing::add);
    }

    public ListSampleService(double trainingRatio, List<Sample> samples) {
        distribute(trainingRatio, samples);
    }

    public void distribute(double trainingRatio, List<Sample> samples) {
        training = take(samples, (int) Math.ceil(samples.size() * trainingRatio));
        testing = samples;
    }

    private static List<Sample> take(List<Sample> samples, int count) {
        double step = (double) samples.size() / count;
        int size = samples.size() - 1;
        List<Sample> res = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            res.add(samples.remove((int) Math.round(size - i * step)));
        }
        return res;
    }

    @Override
    public Iterable<Sample> getTrainingSampling() {
        return training;
    }

    @Override
    public Iterable<Sample> getTestingSampling() {
        return testing;
    }


    @Override
    public List<String> getReport() {
        double size = training.size() + testing.size();
        return List.of(
                "FileSampleService:\n",
                "\ttraining={size=%d, percent=%06.3f%%}\n".formatted(training.size(), training.size() / size),
                "\t testing={size=%d, percent=%06.3f%%}\n".formatted(testing.size(), testing.size() / size)
        );
    }
}
