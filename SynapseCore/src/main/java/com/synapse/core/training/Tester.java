package com.synapse.core.training;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.nets.Net;
import com.synapse.core.samples.Sample;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.synapse.core.tools.DelayedFormatter.format;

@Getter
@Slf4j
public class Tester {

    public static final double OVERFITTING_LIMIT = 0.2;
    /**
     * Коэффициенты формулы для расчета скорости
     */
    private static final int[] COEFFICIENTS = new int[]{+147, -360, +450, -400, +225, -72, +10};

    private final String name;
    private double errorLimit;
    private int maxEpochsCount;
    private double minTestError;
    private List<Double> testingErrors;
    private List<Double> testingPercents;
    private Net bestNet;
    private TrainingResult.StopReason stopReason;

    public Tester(String name, TrainingParameters parameters) {
        this.name = name;
        errorLimit = parameters.getErrorLimit();
        maxEpochsCount = parameters.getMaxEpochsCount();
        minTestError = Double.MAX_VALUE;
        testingErrors = new ArrayList<>();
        testingPercents = new ArrayList<>();
    }

    public void test(Net net, Iterable<Sample> samples, int epoch) {
        log.debug("{} /  TESTING:  STARTED: epoch={}", name, epoch);
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
            log.trace("{} /  TESTING: count={}, error={}, percent={}%, resultIndex={}, targetIndex={}",
                    name,
                    format("%04d", count),
                    format("%07.4f", error / count),
                    format("%07.4f", percent / count * 100),
                    resultIndex, targetIndex
            );
        }

        error /= count;
        percent = percent / count * 100;
        saveTestValues(net, error, percent);
        double speed = getSpeed(testingErrors);

        log.debug("{} /  TESTING: COMPLETED: error={}, percent={}%, speed={}",
                name,
                format("%06.3f", error),
                format("%06.3f", percent),
                format("%07.4f", speed)
        );

        determineResult(epoch, error, speed);
    }

    private void saveTestValues(Net net, double error, double percent) {
        testingErrors.add(error);
        testingPercents.add(percent);
        if (error < minTestError) {
            bestNet = net;
            minTestError = error;
        }
    }

    private void determineResult(int epoch, double error, double speed) {
        if (error < errorLimit) {
            stopReason = TrainingResult.StopReason.MIN_ERROR;
        } else if (epoch >= maxEpochsCount) {
            stopReason = TrainingResult.StopReason.MAX_EPOCH;
        } else if (speed > OVERFITTING_LIMIT) {
            log.debug("{} /  TESTING:  STOPPED: speed={}", name, speed);
            stopReason = TrainingResult.StopReason.OVERFITTING;
        }

    }

    private static int getClassIndex(Matrix matrix) {
        int maxIndex = 0;
        double[] array = matrix.getArray();
        for (int i = 0; i < array.length; i++) {
            maxIndex = array[i] > array[maxIndex] ? i : maxIndex;
        }
        return maxIndex;
    }


    public boolean isContinue() {
        return stopReason == null;
    }

    /**
     * Для расчета скорости изменения функции используется формула
     * численного дифференцирования с 7-ю узлами коэффициентами назад.
     *
     * @param errors значения ошибок обучения, на последних 7 из которых считается скорость.
     * @return значение скорость согласно формуле.
     */
    private double getSpeed(List<Double> errors) {
        int size = errors.size() - 1;
        double speed = 0.0;
        for (int i = 0; i < COEFFICIENTS.length; i++) {
            speed += COEFFICIENTS[i] * getValue(errors, size - i);
        }

        return speed / 60;
    }

    private double getValue(List<Double> errors, int index) {
        return index < 0 ? getValue(errors, index + 1) : errors.get(index);
    }

}
