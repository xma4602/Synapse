package com.synapse.core.activation;

import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class ActivationReLU extends ActivationBase {

    @Serial
    private final static long serialVersionUID = 496749395673840L;

    @Override
    public String getName() {
        return "relu";
    }

    public ActivationReLU(double k) {
        scale = k;
        activator = x -> x > 0 ? scale * x : 0.0;
        deactivator = x -> x > 0 ? scale : 0.0;
    }
}
