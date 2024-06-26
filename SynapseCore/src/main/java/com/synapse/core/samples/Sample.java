package com.synapse.core.samples;

import com.synapse.core.matrix.Matrix;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;


@NoArgsConstructor
@EqualsAndHashCode
public class Sample implements Externalizable {

    @Serial
    private static final long serialVersionUID = -7243488187518186999L;

    @Getter
    private Matrix source;
    @Getter
    private Matrix target;

    public Sample(Matrix[] matrices) {
        this(matrices[0], matrices[1]);
    }

    public Sample(Matrix source, Matrix target) {
        if (source.getRowsNumber() != 1) {
            throw new IllegalArgumentException("Source matrix row length don't equal one and was " + source.getRowsNumber());
        }
        if (target.getRowsNumber() != 1) {
            throw new IllegalArgumentException("Target matrix row length don't equal one and was " + target.getRowsNumber());
        }

        this.source = source;
        this.target = target;
    }

    public int getSourceSize() {
        return source.getColumnsNumber();
    }

    public int getTargetSize() {
        return target.getColumnsNumber();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(source);
        out.writeObject(target);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Matrix source = (Matrix) in.readObject();
        Matrix target = (Matrix) in.readObject();

        this.source = Matrix.translate(source);
        this.target = Matrix.translate(target);
    }

    @Override
    public String toString() {
        return "Sample{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }
}
