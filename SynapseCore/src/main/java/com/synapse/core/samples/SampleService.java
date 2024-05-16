package com.synapse.core.samples;

import com.synapse.core.tools.Reportable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

public interface SampleService extends Reportable {

    Iterable<Sample> getTrainingSampling();

    Iterable<Sample> getTestingSampling();


    static <T> Sampling<T> makeSampling(double trainingFraction, List<T> samples) {
        List<T> training = take(samples, (int) Math.ceil(samples.size() * trainingFraction));
        return new Sampling<>(training, samples);
    }

    @SafeVarargs
    static <T> Sampling<T> makeSampling(double trainingFraction, T... samples) {
        List<T> objects = new LinkedList<>();
        Collections.addAll(objects, samples);
        return makeSampling(trainingFraction, objects);
    }

    private static <T> List<T> take(List<T> samples, int count) {
        double step = (double) samples.size() / count;
        int size = samples.size() - 1;
        List<T> res = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            res.add(samples.remove((int) Math.round(size - i * step)));
        }
        return res;
    }

    @Getter
    @NoArgsConstructor
    class Sampling<T> implements Reportable {


        private Set<T> trainingSamples = new HashSet<>();
        private Set<T> testingSamples = new HashSet<>();

        public Sampling(Collection<T> trainingSamples, Collection<T> testingSamples) {
            this.trainingSamples.addAll(trainingSamples);
            this.testingSamples.addAll(testingSamples);
        }

        public int getSamplingSize() {
            return trainingSamples.size() + testingSamples.size();
        }

        public int getTrainingSize() {
            return trainingSamples.size();
        }

        public int getTestingSize() {
            return testingSamples.size();
        }

        public double getTrainingFraction() {
            return trainingSamples.size() * 1.0 / getSamplingSize();
        }

        public double getTestingFraction() {
            return testingSamples.size() * 1.0 / getSamplingSize();
        }

        @Override
        public List<String> getReport() {
            return List.of(
                    "Sampling:\n",
                    "\t    size=%d\n".formatted(getSamplingSize()),
                    "\ttraining={size=%d, percent=%06.3f%%}\n".formatted(getTrainingSize(), getTrainingFraction()),
                    "\t testing={size=%d, percent=%06.3f%%}\n".formatted(getTestingSize(), getTestingFraction())
            );
        }
    }
}

