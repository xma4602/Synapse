package com.synapse.core.rates;

import lombok.Data;

import java.util.Objects;
@Data
public class LinearRate implements Rate {

    private double startRateValue;

    public LinearRate() {
        this.startRateValue = 10.0;
    }

    public LinearRate(double startRateValue) {
        check(startRateValue);
        this.startRateValue = startRateValue;
    }


    @Override
    public Double apply(int epoch) {
        return startRateValue / (epoch + 1);
    }

    @Override
    public double[] getFactors() {
        return new double[]{startRateValue};
    }

    @Override
    public void setFactors(double... factors) {
        check(factors[0]);
        startRateValue = factors[0];
    }

    private void check(double startRateValue) {
        if (startRateValue <= 0)
            throw new IllegalArgumentException("Argument \"startRateValue\" must be greater than zero, but was " + startRateValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinearRate that)) return false;
        return Double.compare(that.startRateValue, startRateValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startRateValue);
    }
}
