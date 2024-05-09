package com.synapse.core.activation;

import lombok.NoArgsConstructor;

import java.io.Serial;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

@NoArgsConstructor
public class ActivationLogistic extends ActivationBase {

    @Serial
    private final static long serialVersionUID = 7704309848611296761L;

    @Override
    public String getName() {
        return "logistic";
    }

    public ActivationLogistic(double k) {
        scale = k;
        activator = x -> 1 / (1 + exp(-scale * x));
        deactivator = x -> scale * exp(-scale * x) / pow(1 + exp(-scale * x), 2);
    }
}
