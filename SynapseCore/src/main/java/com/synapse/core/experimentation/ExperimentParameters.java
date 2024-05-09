package com.synapse.core.experimentation;

import com.synapse.core.nets.NetParameters;
import com.synapse.core.samples.SampleService;
import com.synapse.core.tools.Reportable;
import com.synapse.core.training.TrainingParameters;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ExperimentParameters implements Cloneable, Externalizable, Reportable {
    @Serial
    private final static long serialVersionUID = 929181984249814L;

    private NetParameters netParameters;
    private TrainingParameters trainingParameters;
    private SampleService sampleService;

    @Override
    public List<String> getReport() {
        List<String> report = new ArrayList<>();
        report.add("ExperimentParameters:\n");
        trainingParameters.getReport().forEach(r -> report.add("\t" + r));
        netParameters.getReport().forEach(r -> report.add("\t" + r));
       // sampleService.getReport().forEach(r -> report.add("\t" + r));

        return report;
    }

    @Override
    public ExperimentParameters clone() {
        try {
            ExperimentParameters clone = (ExperimentParameters) super.clone();
            clone.setSampleService(sampleService);
            clone.setTrainingParameters(trainingParameters.clone());
            clone.setNetParameters(netParameters.clone());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(netParameters);
        out.writeObject(trainingParameters);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        netParameters = (NetParameters) in.readObject();
        trainingParameters = (TrainingParameters) in.readObject();
    }


}
