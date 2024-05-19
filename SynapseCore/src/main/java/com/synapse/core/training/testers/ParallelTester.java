package com.synapse.core.training.testers;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.nets.Net;
import com.synapse.core.samples.Sample;
import com.synapse.core.tools.CoreContext;
import com.synapse.core.training.TrainingParameters;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.synapse.core.tools.DelayedFormatter.format;

@Slf4j
public class ParallelTester extends Tester {

    private ExecutorService executorService = CoreContext.EXECUTOR_SERVICE;

    public ParallelTester(String name, TrainingParameters parameters) {
        super(name, parameters);
    }

    @Override
    protected TestResult performTest(Net net, Iterable<Sample> samples, int epoch) {
        double error = 0;
        double percent = 0;
        int count = 0;
        List<Future<TestResult>> futures = new ArrayList<>();

        for (Sample sample : samples) {
            futures.add(executorService.submit(getTask(net, sample, epoch)));
            count++;
        }
        for (Future<TestResult> future : futures) {
            try {
                TestResult testResult = future.get();
                error += testResult.getPercent();
                percent += testResult.getPercent();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        error = error / count;
        percent = percent / count * 100;
        return new TestResult(error, percent);
    }

    private Callable<TestResult> getTask(Net net, Sample sample, int epoch) {
        return () -> {
            Matrix result = net.pass(sample.getSource());
            Matrix target = sample.getTarget();
            Matrix err = target.sub(result);
            double error = err.sqrsSum() / 2;

            boolean right = getClassIndex(result) == getClassIndex(target);
            double percent = right ? 1 : 0;
            log.trace("{} |  TESTING: epoch={}, error={}, classify={}",
                    name,
                    format("%03d", epoch),
                    format("%.4f", error),
                    right
            );
            return new TestResult(error, percent);
        };
    }
}
