package com.synapse.data.convertation;

import com.synapse.data.dataset.Cifar10;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

public class ConvertFromCifar10BinToImages {

    static String root = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\программа\\datasets\\cifar-10";
    static Path bin = Path.of(root, "bin");
    static Path imagesDir = Path.of(root, "images");

    public static void main(String[] args) throws IOException {
        File[] files = bin.toFile().listFiles();
        for (File file : files) {
            List<BufferedImage> images = Cifar10.readImagesFromBinFile(file);
            IntStream.range(0, images.size()).forEach(i -> {
                try {
                    BufferedImage image = images.get(i);
                    Files.createFile(Path.of(imagesDir.toString(), getName(file, i)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private static String getName(File file, int i) {
        return "%s_%05d".formatted(file.getName(), i);
    }
}
