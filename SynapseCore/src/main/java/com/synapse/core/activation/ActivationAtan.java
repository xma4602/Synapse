package com.synapse.core.activation;

import lombok.NoArgsConstructor;

import static java.lang.Math.*;

@NoArgsConstructor
public class ActivationAtan extends ActivationBase  {

    private static final double m = 2 / PI;

    @Override
    public String getName() {
        return "atan";
    }

    public ActivationAtan(double k) {
        scale = k;
        activator = x -> m * atan(scale * x);
        deactivator = x -> m * scale / (1 + pow(scale * x, 2));
    }
}
