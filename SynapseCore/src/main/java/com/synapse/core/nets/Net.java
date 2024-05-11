package com.synapse.core.nets;

import com.synapse.core.activation.Activation;
import com.synapse.core.matrix.Matrix;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

@Data
@NoArgsConstructor
public class Net implements Cloneable, Externalizable {

    @Serial
    private static final long serialVersionUID = 876676456747560L;

    private Matrix[] weights;
    private Matrix[] biases;
    private Activation[] activations;
    private static final DoubleSupplier DEFAULT_INITIALIZER = () -> Math.random() * 20 - 10;

    public Net(NetParameters parameters) {
        this(parameters.getLayerSizes(), parameters.getActivations());
    }

    public Net(int[] layerSizes, Activation[] activations) {
        if (layerSizes.length - 1 != activations.length)
            throw new IllegalArgumentException("The length of the activations array (%d) must match the number of inner layers (%d)"
                    .formatted(activations.length, layerSizes.length - 1));

        weights = new Matrix[layerSizes.length - 1];
        biases = new Matrix[layerSizes.length - 1];
        this.activations = activations;

        for (int i = 0; i < weights.length; i++) {
            weights[i] = Matrix.create(layerSizes[i], layerSizes[i + 1], DEFAULT_INITIALIZER);
            biases[i] = Matrix.create(1, layerSizes[i + 1], DEFAULT_INITIALIZER);
        }
    }

    public Net(Matrix[] weights, Matrix[] biases, Activation[] activations) {
        if (weights.length != biases.length)
            throw new IllegalArgumentException("The lengths of the arrays of weights (%d) and biases (%d) must match".formatted(weights.length, biases.length));
        if (weights.length + 1 != activations.length)
            throw new IllegalArgumentException("The length of the activations array (%d) must match the number of layers (%d)".formatted(activations.length, weights.length + 1));
        this.weights = weights;
        this.biases = biases;
        this.activations = activations;
    }

    public int getLayersCount() {
        return weights.length + 1;
    }

    public int getInterLayersCount() {
        return weights.length;
    }

    public DoubleFunction<Double>[] getActivators() {
        DoubleFunction<Double>[] activators = new DoubleFunction[activations.length];
        for (int i = 0; i < activations.length; i++) {
            activators[i] = activations[i].getActivator();
        }
        return activators;
    }

    public DoubleFunction<Double>[] getDeactivates() {
        DoubleFunction<Double>[] deactivates = new DoubleFunction[activations.length];
        for (int i = 0; i < activations.length; i++) {
            deactivates[i] = activations[i].getDeactivator();
        }
        return deactivates;
    }

    public Matrix pass(Matrix input) {
        Matrix output = input;
        for (int i = 0; i < weights.length; i++) {
            output = output.mul(weights[i]).add(biases[i]).apply(activations[i].getActivator());
        }
        return output;
    }

    public NetBuilder builder() {
        return new NetBuilder();
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
                clone.activations[i] = activations[i].clone();
            }
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
            out.writeObject(activations[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int length = in.readInt();
        weights = new Matrix[length];
        biases = new Matrix[length];
        activations = new Activation[length];
        for (int i = 0; i < length; i++) {
            weights[i] = (Matrix) in.readObject();
            biases[i] = (Matrix) in.readObject();
            activations[i] = (Activation) in.readObject();
        }
    }

    public int[] getLayersSizes() {
        int[] sizes = new int[getLayersCount()];
        sizes[0] = weights[0].getRowLength();
        for (int i = 0; i < weights.length; i++) {
            sizes[i + 1] = weights[i].getColumnLength();
        }
        return sizes;
    }

    private static class NetBuilder {

        List<Integer> layerSizes;
        List<Activation> activations;

        public NetBuilder() {
            layerSizes = new LinkedList<>();
            activations = new LinkedList<>();
        }

        public NetBuilder addLayer(int size, Activation activation) {
            layerSizes.add(size);
            activations.add(activation);
            return this;
        }

        public Net build() {
            int[] layers = layerSizes.stream().mapToInt(x -> x).toArray();
            return new Net(layers, activations.toArray(new Activation[0]));
        }
    }

}
