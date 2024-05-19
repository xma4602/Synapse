package com.synapse.core.nets;

import com.synapse.core.activation.Activation;
import com.synapse.core.matrix.Matrix;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

@Data
@NoArgsConstructor
public class Net implements Cloneable, Externalizable {

    @Serial
    private static final long serialVersionUID = 876676456747560L;

    private Matrix[] weights;
    private Matrix[] biases;
    private Activation activation;
    private static final DoubleSupplier DEFAULT_INITIALIZER = () -> Math.random() * 20 - 10;

    public Net(NetParameters parameters) {
        this(parameters.getLayerSizes(), parameters.getActivation());
    }

    public Net(int[] layerSizes, Activation activation) {
        weights = new Matrix[layerSizes.length - 1];
        biases = new Matrix[layerSizes.length - 1];
        this.activation = activation;

        for (int i = 0; i < weights.length; i++) {
            weights[i] = Matrix.create(layerSizes[i], layerSizes[i + 1], DEFAULT_INITIALIZER);
            biases[i] = Matrix.create(1, layerSizes[i + 1], DEFAULT_INITIALIZER);
        }
    }

    public Net(Matrix[] weights, Matrix[] biases, Activation activation) {
        if (weights.length != biases.length)
            throw new IllegalArgumentException("The lengths of the arrays of weights (%d) and biases (%d) must match".formatted(weights.length, biases.length));
         this.weights = weights;
        this.biases = biases;
        this.activation = activation;
    }

    public int getLayersCount() {
        return weights.length + 1;
    }

    public int getInterLayersCount() {
        return weights.length;
    }

    public DoubleFunction<Double> getActivator() {
        return activation.getActivator();
    }

    public DoubleFunction<Double> getDeactivator() {
        return activation.getDeactivator();
    }

    public Matrix pass(Matrix input) {
        Matrix output = input;
        for (int i = 0; i < weights.length; i++) {
            output = output.mul(weights[i]).add(biases[i]).apply(activation.getActivator());
        }
        return output;
    }

    @Override
    public Net clone() {
        try {
            Net clone = (Net) super.clone();
            clone.weights = new Matrix[weights.length];
            clone.biases = new Matrix[biases.length];
            for (int i = 0; i < weights.length; i++) {
                clone.weights[i] = weights[i].clone();
                clone.biases[i] = biases[i].clone();
            }
            clone.activation = activation;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(weights.length);
        for (int i = 0; i < weights.length; i++) {
            out.writeObject(weights[i]);
            out.writeObject(biases[i]);
        }
        out.writeObject(activation);

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int length = in.readInt();
        weights = new Matrix[length];
        biases = new Matrix[length];
        for (int i = 0; i < length; i++) {
            weights[i] = (Matrix) in.readObject();
            biases[i] = (Matrix) in.readObject();
        }
        activation = (Activation) in.readObject();

    }

    public int[] getLayersSizes() {
        int[] sizes = new int[getLayersCount()];
        sizes[0] = weights[0].getRowsNumber();
        for (int i = 0; i < weights.length; i++) {
            sizes[i + 1] = weights[i].getColumnsNumber();
        }
        return sizes;
    }

}
