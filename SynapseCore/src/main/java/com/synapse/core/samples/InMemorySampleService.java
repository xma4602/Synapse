package com.synapse.core.samples;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class InMemorySampleService implements SampleService {

    private Sampling<Sample> sampling = new Sampling<>();

    public InMemorySampleService(SampleService sampleService) {
        this(sampleService.getTrainingSampling(), sampleService.getTestingSampling());
    }

    public InMemorySampleService(Iterable<Sample> trainingSamples, Iterable<Sample> testingSamples) {
        trainingSamples.forEach(sampling.getTrainingSamples()::add);
        testingSamples.forEach(sampling.getTestingSamples()::add);
    }

    public InMemorySampleService(double trainingRatio, List<Sample> samples) {
        sampling = SampleService.makeSampling(trainingRatio, samples);
    }


    @Override
    public Iterable<Sample> getTrainingSampling() {
        return sampling.getTrainingSamples();
    }

    @Override
    public Iterable<Sample> getTestingSampling() {
        return sampling.getTestingSamples();
    }


    @Override
    public List<String> getReport() {
        List<String> report = List.of("FileSampleService:\n");
        addSubReports(report, sampling.getReport());
        return report;
    }
}
