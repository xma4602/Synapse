package com.synapse.core.training;

import com.synapse.core.rates.Rate;
import com.synapse.core.tools.Reportable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

@Data
@NoArgsConstructor
public class TrainingParameters implements Cloneable, Externalizable, Reportable {

    private Rate rate;
    private int maxEpochsCount;
    private int batchSize;
    private double errorLimit;

    @Override
    public TrainingParameters clone() {
        try {
            TrainingParameters clone = (TrainingParameters) super.clone();
            clone.setMaxEpochsCount(maxEpochsCount);
            clone.setRate(rate);
            clone.setErrorLimit(errorLimit);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(maxEpochsCount);
        out.writeInt(batchSize);
        out.writeDouble(errorLimit);
        out.writeObject(rate);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        maxEpochsCount = in.readInt();
        batchSize = in.readInt();
        errorLimit = in.readDouble();
        rate = (Rate) in.readObject();

    }

    @Override
    public List<String> getReport() {
        return List.of(
                "TrainingParameters:\n",
                "\tmaxEpochsCount=%s\n".formatted(maxEpochsCount),
                "\tbatchSize=%s\n".formatted(batchSize),
                "\terrorLimit=%s\n".formatted(errorLimit),
                "\trate=%s\n".formatted(rate)
        );
    }
}
