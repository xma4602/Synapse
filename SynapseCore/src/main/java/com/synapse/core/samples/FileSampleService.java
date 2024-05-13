package com.synapse.core.samples;

import lombok.NoArgsConstructor;

import java.io.*;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor
public class FileSampleService implements SampleService {

    private Sampling<File> sampling = new Sampling<>();
    private int trainingItemsLimit;
    private int testingItemsLimit;

    public FileSampleService(Sampling<File> sampling, int trainingLimit, int testingLimit) {
        this.sampling = sampling;
        trainingItemsLimit = trainingLimit;
        testingItemsLimit = testingLimit;
    }

    public FileSampleService(Sampling<File> sampling) {
        this(sampling, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public FileSampleService(List<File> trainingFiles, int trainingLimit,
                             List<File> testingFiles, int testingLimit) {
        this(new Sampling<>(trainingFiles, testingFiles), trainingLimit, testingLimit);
    }

    public FileSampleService(List<File> trainingFiles, List<File> testingFiles) {
        this(trainingFiles, Integer.MAX_VALUE, testingFiles, Integer.MAX_VALUE);
    }

    public void setTrainingFiles(File... trainingFiles) {
        setTrainingFile(Integer.MAX_VALUE, trainingFiles);
    }

    public void setTestingFiles(File... testingFiles) {
        setTestingFile(Integer.MAX_VALUE, testingFiles);
    }

    public void setTrainingFile(int trainingItemsLimit, File... trainingFiles) {
        this.sampling.setTrainingSamples(List.of(trainingFiles));
        this.trainingItemsLimit = trainingItemsLimit;
    }

    public void setTestingFile(int testingItemsLimit, File... testingFiles) {
        this.sampling.setTestingSamples(List.of(testingFiles));
        this.testingItemsLimit = testingItemsLimit;
    }

    @Override
    public List<String> getReport() {
        List<String> report = List.of(
                "FileSampleService:\n",
                "\ttraining items limit=%s\n".formatted(trainingItemsLimit),
                "\ttesting  items limit=%s\n".formatted(testingItemsLimit)
        );
        addSubReports(report, sampling.getReport());
        return report;
    }

    @Override
    public Iterable<Sample> getTrainingSampling() {
        return () -> new SampleReader(trainingItemsLimit, sampling.getTrainingSamples());
    }

    @Override
    public Iterable<Sample> getTestingSampling() {
        return () -> new SampleReader(testingItemsLimit, sampling.getTestingSamples());
    }

    private static class SampleReader implements Iterator<Sample> {
        private int limit;
        private Iterator<File> files;
        private Sample sample;
        private int count;
        private ObjectInputStream in;

        public SampleReader(int itemsLimit, Iterable<File> fileList) {
            limit = itemsLimit;
            files = fileList.iterator();
            sample = null;
            count = 0;
            if (files.hasNext()) {
                try {
                    in = openStream(files.next());
                    sample = (Sample) in.readObject();
                } catch (EOFException ignored) {
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        private ObjectInputStream openStream(File file) {
            try {
                return new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public boolean hasNext() {
            return count < limit && in != null && sample != null;
        }

        @Override
        public Sample next() {
            Sample oldSample = null;
            try {
                oldSample = sample;
                sample = (Sample) in.readObject();
            } catch (EOFException e) {
                in.close();
                in = null;
                if (files.hasNext()) {
                    in = openStream(files.next());
                    sample = (Sample) in.readObject();
                }
            } finally {
                count++;
                return oldSample;
            }
        }
    }

}
