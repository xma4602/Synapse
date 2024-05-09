package com.synapse.core.rates;

import java.io.Serializable;
import java.util.function.IntFunction;

public interface Rate extends IntFunction<Double>, Serializable {
    static Rate getDefault() {
        return new ConstantRate(1);
    }

    double[] getFactors();

    void setFactors(double... factors);
}
