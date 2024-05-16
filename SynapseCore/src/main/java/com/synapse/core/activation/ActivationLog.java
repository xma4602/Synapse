package com.synapse.core.activation;

import lombok.NoArgsConstructor;

import java.io.Serial;

import static java.lang.Math.exp;

@NoArgsConstructor
public class ActivationLog extends ActivationBase {

    @Serial
    private final static long serialVersionUID = 7704309848611296761L;

    @Override
    public String getName() {
        return "logistic";
    }

    public ActivationLog(double k) {
        scale = k;
        activator = x -> 1.0 / (1.0 + exp(-scale * x));
        deactivator = x -> {
            double exp = activator.apply(x);
            return scale * exp * (1 - exp);
        };
    }
}
