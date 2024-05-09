package com.synapse.data.learning;

import com.synapse.core.experimentation.ExperimentResult;

import java.io.*;

public class OpenExperiment {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String path = "C:\\Users\\User\\Desktop\\Михаил Ханов\\cifar-10\\experimentation\\2024-05-10-00-20-04_result.exp.res";

        try (var in = new ObjectInputStream(new FileInputStream(path))) {
            ExperimentResult result = (ExperimentResult) in.readObject();
            result.printReport();
        }

    }
}

