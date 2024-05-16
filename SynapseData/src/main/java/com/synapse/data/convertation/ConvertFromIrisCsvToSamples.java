package com.synapse.data.convertation;

import com.synapse.core.samples.Sample;
import com.synapse.data.dataset.Iris;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConvertFromIrisCsvToSamples {

    public static void main(String[] args) throws IOException {
        String root = "C:\\Users\\xma4602\\Documents\\ВУЗ\\Диплом\\программа\\datasets\\iris";
        File sourceFile = Path.of(root, "csv", "iris.csv").toFile();
        File resultFile = Path.of(root, "samples", "data.sample").toFile();

        List<Sample> samples = Iris.convert(sourceFile);
        Files.createFile(resultFile.toPath());
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resultFile))) {
            for (Sample sample : samples) {
                out.writeObject(sample);
            }
        }
    }

}
