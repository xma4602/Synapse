package com.synapse.data.dataset;

import com.synapse.core.nets.Matrix;
import com.synapse.core.samples.Sample;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Iterator;

public class Cifar10 {

    public static Iterable<Sample> readBinFile(File file) throws FileNotFoundException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        return () -> new Iterator<>() {

            @Override
            @SneakyThrows
            public boolean hasNext() {
                if (in.available() > 0) {
                    return true;
                } else {
                    in.close();
                    return false;
                }
            }

            @Override
            @SneakyThrows
            public Sample next() {
                Matrix target = nextTarget();
                Matrix source = nextSource();
                return new Sample(source, target);
            }

            private Matrix nextTarget() throws IOException {
                double[] doubles = new double[10];
                int index = in.read();
                doubles[index] = 1;
                return new Matrix(doubles);
            }

            private Matrix nextSource() throws IOException {
                byte[] bytes = in.readNBytes(3072);
                double[] doubles = new double[bytes.length];
                for (int i = 0; i < bytes.length / 3; i++) {
                    doubles[i] = getUnsignedDouble(bytes[i]);
                    doubles[i + 1] = getUnsignedDouble(bytes[i + 1024]);
                    doubles[i + 2] = getUnsignedDouble(bytes[i + 2048]);
                }
                return new Matrix(doubles);
            }

            private static double getUnsignedDouble(byte value) {
                return (value & 0xFF) / 255.0;
            }

        };
    }
}
