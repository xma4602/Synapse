package com.synapse.data.dataset;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.samples.Sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Iris {

    public static List<Sample> convert(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream()
                .map(s -> s.split(","))
                .map(Iris::convert)
                .toList();
    }

    private static final List<String> types = List.of(
            "Iris-setosa", "Iris-versicolor", "Iris-virginica"
    );

    private static Sample convert(String[] line) {
        double[] source = new double[4];
        double[] target = new double[3];
        for (int i = 0; i < line.length - 1; i++) {
            source[i] = Double.parseDouble(line[i]);
        }
        target[types.indexOf(line[4])] = 1.0;
        return new Sample(Matrix.create(source), Matrix.create(target));
    }
}
