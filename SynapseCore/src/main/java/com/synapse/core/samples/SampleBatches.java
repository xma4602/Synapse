package com.synapse.core.samples;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Iterator;

@RequiredArgsConstructor

public class SampleBatches implements Iterable<Iterable<Sample>> {

    private final int batchSize;
    private final Iterable<Sample> sampleSource;

    @Override
    public Iterator<Iterable<Sample>> iterator() {
        return new SampleBatchesIterator(batchSize, sampleSource.iterator());
    }
    @RequiredArgsConstructor
    @ToString
    private static class SampleBatchesIterator implements Iterator<Iterable<Sample>>{

        private final int batchSize;
        private final Iterator<Sample> sampleIterator;

        @Override
        public boolean hasNext() {
            return sampleIterator.hasNext();
        }

        @Override
        public Iterable<Sample> next() {
            return () -> new Iterator<>() {

                int batchCount = 0;

                @Override
                public boolean hasNext() {
                    return batchCount < batchSize && sampleIterator.hasNext();
                }

                @Override
                public Sample next() {
                    batchCount++;
                    return sampleIterator.next();
                }
            };
        }
    }

}
