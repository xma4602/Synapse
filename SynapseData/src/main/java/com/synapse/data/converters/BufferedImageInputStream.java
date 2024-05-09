package com.synapse.data.converters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class BufferedImageInputStream implements AutoCloseable {

    private BufferedInputStream in;

    public BufferedImageInputStream(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }
    public BufferedImageInputStream(FileInputStream inputStream) {
        in = new BufferedInputStream(inputStream);
    }

    public BufferedImage readImage() throws IOException {
        return ImageIO.read(in);
    }

    @Override
    public void close() throws Exception {
        in.close();
    }
}
