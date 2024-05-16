package com.synapse.data.dataset;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.samples.Sample;
import com.synapse.data.converters.ImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Digiface {
    public static Sample convert(File file, int targetIndex, int targetSize) throws IOException {
        BufferedImage image = ImageIO.read(file);
        Matrix source = ImageService.convertRGB(image, false);
        double[] doubles = new double[targetSize];
        doubles[targetIndex] = 1;
        Matrix target = Matrix.create(doubles);
        return new Sample(source, target);
    }
}
