package com.synapse.core.activation;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleFunction;

public abstract class ActivationBase implements Activation {

    protected double scale;
    protected DoubleFunction<Double> activator;
    protected DoubleFunction<Double> deactivator;

    @Override
    public DoubleFunction<Double> getActivator() {
        return activator;
    }

    @Override
    public DoubleFunction<Double> getDeactivator() {
        return deactivator;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public Activation clone() {
        try {
            ActivationBase clone = (ActivationBase) super.clone();
            clone.activator = activator;
            clone.deactivator = deactivator;
            clone.scale = scale;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!o.getClass().equals(getClass())) return false;
        return Double.compare(((Activation) o).getScale(), getScale()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName(), getScale());
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        DoubleFunction<Double> function;
        function = (DoubleFunction<Double> & Serializable) (arg -> activator.apply(arg));
        out.writeObject(function);
        function = (DoubleFunction<Double> & Serializable) (arg -> deactivator.apply(arg));
        out.writeObject(function);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        activator = (DoubleFunction<Double>) in.readObject();
        deactivator = (DoubleFunction<Double>) in.readObject();
    }

    @Override
    public String toString() {
        return "%s(%f)".formatted(getName(), getScale());
    }
}
