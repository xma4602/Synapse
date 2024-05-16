package com.synapse.core.activation;

import java.io.Externalizable;
import java.util.Arrays;
import java.util.function.DoubleFunction;

public interface Activation extends Cloneable, Externalizable {
    DoubleFunction<Double> getActivator();

    DoubleFunction<Double> getDeactivator();

    double getScale();

    void setScale(double scale);

    Activation clone();

    default String getName() {
        return "activation";
    }

    static Activation getDefault() {
        return new ActivationLog(0.2);
    }

    static Activation[] arrayOf(Activation activation, int length) {
        Activation[] activations = new Activation[length];
        Arrays.fill(activations, activation);
        return activations;
    }

}
