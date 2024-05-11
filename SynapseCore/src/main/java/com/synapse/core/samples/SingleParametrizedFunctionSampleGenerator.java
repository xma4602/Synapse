package com.synapse.core.samples;

import com.synapse.core.matrix.Matrix;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleFunction;

@RequiredArgsConstructor
public class SingleParametrizedFunctionSampleGenerator implements SampleGenerator {
    private final DoubleFunction<Double> function;
    private final double[] data;

    @Override
    public List<Sample> generate() {
        List<Sample> samples = new LinkedList<>();
        for (var x : data) {
            samples.add(new Sample(
                    Matrix.create(1, 1, x),
                    Matrix.create(1, 1, function.apply(x))
            ));
        }
        return samples;
    }
}
