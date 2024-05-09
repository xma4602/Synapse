package com.synapse.core.samples;

import java.util.Iterator;

public class SampleBatches implements Iterable<Iterable<Sample>> {

    private final int batchSize;
    private final Iterable<Sample> sampleSource;

    public SampleBatches(int batchSize, Iterable<Sample> samples) {
        this.batchSize = batchSize;
        this.sampleSource = samples;
    }

    @Override
    public Iterator<Iterable<Sample>> iterator() {
        Iterator<Sample> iterator = sampleSource.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Iterable<Sample> next() {
                return () -> new Iterator<Sample>() {

                    int batchCount = 0;

                    @Override
                    public boolean hasNext() {
                        return batchCount < batchSize && iterator.hasNext();
                    }

                    @Override
                    public Sample next() {
                        batchCount++;
                        return iterator.next();
                    }
                };
            }
        };
    }
}
