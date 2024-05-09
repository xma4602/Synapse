package com.synapse.core.rates;

import lombok.Data;

import java.util.Objects;

@Data
public class ConstantRate implements Rate {

    private double constRateValue;

    public ConstantRate(double constRateValue) {
        check(constRateValue);
        this.constRateValue = constRateValue;
    }

    @Override
    public Double apply(int epoch) {
        return constRateValue;
    }

    @Override
    public double[] getFactors() {
        return new double[]{constRateValue};
    }

    @Override
    public void setFactors(double... factors) {
        check(factors[0]);
        constRateValue = factors[0];
    }

    private void check(double constRateValue) {
        if (constRateValue <= 0)
            throw new IllegalArgumentException("Argument \"constRateValue\" must be greater than zero, but was " + constRateValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstantRate that)) return false;
        return Double.compare(that.constRateValue, constRateValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constRateValue);
    }
}
