package com.synapse.core.activation;

import lombok.NoArgsConstructor;

import static java.lang.Math.*;

@NoArgsConstructor
public class ActivationTanh extends ActivationBase {

    @Override
    public String getName() {
        return "tanh";
    }

    public ActivationTanh(double k) {
        scale = k;
        activator = x -> tanh(scale * x);
        deactivator = x -> 1 - pow(tanh(scale * x), 2.0);
    }
}
