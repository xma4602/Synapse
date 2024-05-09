package com.synapse.data.dataset;

import com.synapse.core.nets.Matrix;
import com.synapse.core.samples.Sample;
import com.synapse.data.converters.BufferedImageInputStream;
import com.synapse.data.converters.ImageService;

import java.io.File;
import java.io.IOException;

public class Digiface {
    public static Sample convert(File file, int targetIndex, int targetSize) throws IOException {
        try (BufferedImageInputStream in = new BufferedImageInputStream(file)) {
            Matrix source = ImageService.convertRGB(in.readImage(), false);
            double[] doubles = new double[targetSize];
            doubles[targetIndex] = 1;
            Matrix target = new Matrix(doubles);
            return new Sample(source, target);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
