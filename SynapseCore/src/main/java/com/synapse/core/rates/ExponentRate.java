package com.synapse.core.rates;

import lombok.Data;

import java.util.Objects;
@Data

public class ExponentRate implements Rate {

    private double height;
    private double curve;

    public ExponentRate() {
        this.height = 1.0;
        this.curve = 1.0;
    }

    public ExponentRate(double height, double curve) {
        check(height, curve);
        this.height = height;
        this.curve = curve;
    }

    @Override
    public Double apply(int epoch) {
        return height * Math.exp(-curve * epoch);
    }

    @Override
    public double[] getFactors() {
        return new double[]{height, curve};
    }

    @Override
    public void setFactors(double... factors) {
        check(factors[0], factors[1]);
        height = factors[0];
        curve = factors[1];

    }

    private void check(double height, double curve) {
        if (height <= 0)
            throw new IllegalArgumentException("Argument \"height\" must be greater than zero, but was " + height);
        if (curve <= 0)
            throw new IllegalArgumentException("Argument \"curve\" must be greater than zero, but was " + curve);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExponentRate that)) return false;
        return Double.compare(that.height, height) == 0 && Double.compare(that.curve, curve) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, curve);
    }
}
