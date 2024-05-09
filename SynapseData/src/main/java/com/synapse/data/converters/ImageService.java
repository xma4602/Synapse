package com.synapse.data.converters;

import com.synapse.core.nets.Matrix;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageService {

    public static Matrix convertRGB(BufferedImage source, boolean withAlphaChannel) {
        return new Matrix(ImageService.getArrayRGB_withoutAlpha(source, withAlphaChannel));
    }

    private static double[] getArrayRGB_withoutAlpha(BufferedImage image, boolean withAlphaChannel) {
        byte[] pixels = loadPixels(image);
        boolean hasAlphaChannel = image.getAlphaRaster() != null;

        if (hasAlphaChannel && !withAlphaChannel) {
            return getArrayRGB_withoutAlpha(pixels);
        } else {
            return getArrayARGB(pixels);
        }
    }

    private static double[] getArrayRGB_withoutAlpha(byte[] pixels) {
        double[] array = new double[pixels.length / 4 * 3];
        for (int i = 0; i < array.length; i++) {
            array[i] = getUnsignedDouble(pixels[i + i / 3 + 1]);
        }
        return array;
    }

    private static double[] getArrayARGB(byte[] pixels) {
        double[] array = new double[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            array[i] = getUnsignedDouble(pixels[i]);
        }
        return array;
    }
    private static double getUnsignedDouble(byte value) {
        return (value & 0xFF) / 255.0;
    }

    public static byte[] loadPixels(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

//
//    private static double getA(int rgb) {
//        return (double) ((rgb >> 24) & 0xff) / 0xff;
//    }
//
//    private static double getR(int rgb) {
//        return (double) ((rgb >> 16) & 0xff) / 0xff;
//    }
//
//    private static double getG(int rgb) {
//        return (double) ((rgb >> 8) & 0xff) / 0xff;
//    }
//
//    private static double getB(int rgb) {
//        return (double) ((rgb) & 0xff) / 0xff;
//    }
//
//    private static double getBW(int rgb) {
//        return (getR(rgb) + getB(rgb) + getG(rgb)) / 3;
//    }

}
