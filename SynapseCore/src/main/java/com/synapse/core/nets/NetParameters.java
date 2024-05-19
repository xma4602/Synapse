package com.synapse.core.nets;

import com.synapse.core.activation.Activation;
import com.synapse.core.tools.Reportable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetParameters implements Cloneable, Externalizable, Reportable {
    private int[] layerSizes;
    private Activation activation;

    public Net createNet() {
        return new Net(layerSizes, activation);
    }

    @Override
    public NetParameters clone() {
        try {
            NetParameters clone = (NetParameters) super.clone();
            clone.setLayerSizes(layerSizes);
            clone.setActivation(activation);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(layerSizes.length);
        for (int layerSize : layerSizes) {
            out.writeInt(layerSize);
        }
        out.writeObject(activation);

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        layerSizes = new int[in.readInt()];
        for (int i = 0; i < layerSizes.length; i++) {
            layerSizes[i] = in.readInt();
        }
        activation = (Activation) in.readObject();
    }

    @Override
    public List<String> getReport() {
        return List.of(
                "NetParameters:\n",
                "\tlayerSizes=%s\n".formatted(Arrays.toString(layerSizes)),
                "\tactivation=%s\n".formatted(activation)
        );
    }
}