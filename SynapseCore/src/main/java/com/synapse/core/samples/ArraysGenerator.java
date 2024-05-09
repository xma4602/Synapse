package com.synapse.core.samples;

public class ArraysGenerator {
    public static double[] range(double start, double end, double step) {
        if (end < start) {
            throw new IllegalArgumentException("Value start=%f greater then end=%f".formatted(start, end));
        }
        int count = (int) Math.round((end - start) / step);
        return getData(start, step, count);
    }

    public static double[] space(double start, double end, int count) {
        if (end < start) {
            throw new IllegalArgumentException("Value start=%f greater then end=%f".formatted(start, end));
        }
        double step = (end - start) / count;
        return getData(start, step, count);
    }

    private static double[] getData(double start, double step, int count) {
        double[] data = new double[count];
        for (int i = 0; i < count; i++) {
            data[i] = start + i * step;
        }

        return data;
    }

    public static double[] rangeLog(double start, double end, double base) {
        if (end < start) {
            throw new IllegalArgumentException("Value start=%f greater then end=%f".formatted(start, end));
        }
        int count = (int) Math.round(log(end / start, base));
        return getDataLog(start, end, base, count);
    }

    public static double[] spaceLog(double start, double end, int count) {
        if (end < start) {
            throw new IllegalArgumentException("Value start=%f greater then end=%f".formatted(start, end));
        }
        return getDataLog(start, end, 10, count);
    }

    private static double[] getDataLog(double start, double end, double base, int count) {
        double[] data = new double[count];
        end -= start;
        for (int i = 0; i < count; i++) {
            data[i] = start + end * (1 + log((double) (i + 1) / count, base));
        }

        return data;
    }

    private static double log(double x, double base) {
        return Math.log(x) / Math.log(base);
    }
}
