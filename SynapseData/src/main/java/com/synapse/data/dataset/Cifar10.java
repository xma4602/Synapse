package com.synapse.data.dataset;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.samples.Sample;

import java.io.*;
import java.util.Iterator;

public class Cifar10 {

    public static final int IMAGE_SIZE = 3072;

//    public static List<BufferedImage> readImagesFromBinFile(File file) {
//        List<BufferedImage> images = new ArrayList<>();
//        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
//            while (true) {
//                in.read();
//                byte[] bytes = in.readNBytes(IMAGE_SIZE);
//                bytes = formatBytes(bytes);
//                BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
//                WritableRaster raster = (WritableRaster) image.getData();
//                raster.setPixels(0, 0, 32, 32, bytes);
//                images.add(image);
//            }
//        } catch (EOFException ignored) {
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return images;
//    }

    private static byte[] formatBytes(byte[] bytes) {
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length / 3; i++) {
            b[i] = bytes[i];
            b[i + 1] = bytes[i + 1024];
            b[i + 2] = bytes[i + 2048];
        }
        return b;
    }


    public static Iterable<Sample> readSamplesFromBinFile(File file) throws FileNotFoundException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        return () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                try {
                    return in.available() >= IMAGE_SIZE;
                } catch (EOFException e) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    return false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Sample next() {
                try {
                    Matrix target = nextTarget();
                    Matrix source = nextSource();
                    return new Sample(source, target);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private Matrix nextTarget() throws IOException {
                double[] doubles = new double[10];
                int index = in.read();
                doubles[index] = 1;
                return Matrix.create(doubles);
            }

            private Matrix nextSource() throws IOException {
                byte[] bytes = in.readNBytes(3072);
                double[] doubles = new double[bytes.length];
                for (int i = 0; i < bytes.length / 3; i++) {
                    doubles[i] = getUnsignedDouble(bytes[i]);
                    doubles[i + 1] = getUnsignedDouble(bytes[i + 1024]);
                    doubles[i + 2] = getUnsignedDouble(bytes[i + 2048]);
                }
                return Matrix.create(doubles);
            }

            private static double getUnsignedDouble(byte value) {
                return (value & 0xFF) / 255.0;
            }

        };
    }
}
