package com.synapse.core.samples;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class FileSampleService implements SampleService {

    private List<File> trainingFiles;
    private int trainingItemsLimit;
    private List<File> testingFiles;
    private int testingItemsLimit;

    @Override
    public List<String> getReport() {
        List<String> report = new ArrayList<>();
        report.add("FileSampleService:\n");
        report.add("\ttraining items limit=%s\n".formatted(trainingItemsLimit));
        report.add("\ttraining files:\n");
        for (int i = 0; i < trainingFiles.size(); i++) {
            report.add("\t\ttraining file %d=%s\n".formatted(i, trainingFiles.get(i).getPath()));
        }
        report.add("\ttesting items limit=%s\n".formatted(testingItemsLimit));
        report.add("\ttesting files:\n");
        for (int i = 0; i < testingFiles.size(); i++) {
            report.add("\t\ttesting file %d=%s\n".formatted(i, testingFiles.get(i).getPath()));
        }

        return report;
    }

    public void setTrainingFiles(File... trainingFiles) {
        setTrainingFile(Integer.MAX_VALUE, trainingFiles);
    }

    public void setTestingFiles(File... testingFiles) {
        setTestingFile(Integer.MAX_VALUE, testingFiles);
    }

    public void setTrainingFile(int trainingItemsLimit, File... trainingFiles) {
        this.trainingFiles = List.of(trainingFiles);
        this.trainingItemsLimit = trainingItemsLimit;
    }

    public void setTestingFile(int testingItemsLimit, File... testingFiles) {
        this.testingFiles = List.of(testingFiles);
        this.testingItemsLimit = testingItemsLimit;
    }

    @Override
    public Iterable<Sample> getTrainingSampling() {
        return () -> new SampleReader(trainingItemsLimit, trainingFiles);
    }

    @Override
    public Iterable<Sample> getTestingSampling() {
        return () -> new SampleReader(testingItemsLimit, testingFiles);
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
