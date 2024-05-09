package com.synapse.core.nets;

import com.synapse.core.activation.Activation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NetTest {

    Net net;
    int[] layers;
    Activation[] activations;

    @BeforeEach
    void setUp() {
        layers = new int[]{5, 4, 3, 2};
        activations = Activation.arrayOf(Activation.getDefault(), layers.length);
        net = new Net(layers, activations);
    }

    @Test
    void pass() {
        Matrix matrix = new Matrix(layers[0]);
        Matrix result = net.pass(matrix);
    }
}