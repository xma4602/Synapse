package com.synapse.core.training.testers;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.nets.Net;
import com.synapse.core.samples.Sample;
import com.synapse.core.training.TrainingParameters;
import lombok.extern.slf4j.Slf4j;

import static com.synapse.core.tools.DelayedFormatter.format;

@Slf4j
public class SerialTester extends Tester {

    public SerialTester(String name, TrainingParameters parameters) {
        super(name, parameters);
    }

    @Override
    protected Tester.TestResult performTest(Net net, Iterable<Sample> samples, int epoch) {
        double error = 0;
        double percent = 0;
        int count = 0;

        for (Sample sample : samples) {
            Matrix result = net.pass(sample.getSource());
            Matrix target = sample.getTarget();
            Matrix err = target.sub(result);
            error += err.sqrsSum() / 2;

            int resultIndex = getClassIndex(result);
            int targetIndex = getClassIndex(target);
            if (resultIndex == targetIndex) percent++;
            count++;
            log.trace("{} |  TESTING: epoch={}, count={}, error={}, percent={}%",
                    name,
                    format("%03d", epoch),
                    format("%04d", count),
                    format("%.4f", error),
                    format("%08.4f", percent * 100)
            );
        }

        error /= count;
        percent = percent / count * 100;
        return new TestResult(error, percent);
    }

}
