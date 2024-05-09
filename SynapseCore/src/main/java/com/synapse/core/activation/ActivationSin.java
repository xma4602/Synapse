package com.synapse.core.activation;

import lombok.NoArgsConstructor;

import static java.lang.Math.*;

@NoArgsConstructor
public class ActivationSin extends ActivationBase {

    @Override
    public String getName() {
        return "sin";
    }

    public ActivationSin(double k) {
        scale = k;
        activator = x -> {
            if (x < -PI / (2 * scale)) return -1.0;
            if (x > +PI / (2 * scale)) return +1.0;
            return sin(scale * x);
        };
        deactivator = x -> {
            if (x < -PI / (2 * scale)) return 0.0;
            if (x > +PI / (2 * scale)) return 0.0;
            return scale * cos(scale * x);
        };
    }
}
