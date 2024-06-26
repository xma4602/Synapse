package com.synapse.core.training.testers;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.nets.Net;
import com.synapse.core.samples.Sample;
import com.synapse.core.training.TrainingParameters;
import com.synapse.core.training.TrainingResult;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.synapse.core.tools.DelayedFormatter.format;

@Getter
@Slf4j
@NoArgsConstructor
public abstract class Tester {

    public static final double OVERFITTING_LIMIT = 1.0;

    @Setter
    protected String name;
    protected double errorLimit;
    protected int maxEpochsCount;
    protected double minTestError;
    protected List<Double> testingErrors;
    protected List<Double> testingPercents;
    protected Net bestNet;
    protected TrainingResult.StopReason stopReason;

    public void setTrainingParameters(TrainingParameters parameters){
        errorLimit = parameters.getErrorLimit();
        maxEpochsCount = parameters.getMaxEpochsCount();
        minTestError = Double.MAX_VALUE;
        testingErrors = new ArrayList<>();
        testingPercents = new ArrayList<>();
    }

    public void test(Net net, Iterable<Sample> samples, int epoch) {
        log.debug("{} |  TESTING:   STARTED: epoch={}", name, epoch);

        TestResult testResult = performTest(net, samples, epoch);

        saveTestValues(net, testResult.getError(), testResult.getPercent());
        double speed = getSpeed(testingErrors);

        log.debug("{} |  TESTING: COMPLETED: error={}, percent={}%, speed={}",
                name,
                format("%06.3f", testResult.getError()),
                format("%06.3f", testResult.getPercent()),
                format("%07.4f", speed)
        );

        determineResult(epoch, testResult.getError(), speed);
    }

    protected abstract TestResult performTest(Net net, Iterable<Sample> samples, int epoch);


    @Value
    protected static class TestResult {
        double error;
        double percent;
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
            log.debug("{} |  TESTING:  STOPPED: speed={}", name, speed);
            stopReason = TrainingResult.StopReason.OVERFITTING;
        }
    }

    public static int getClassIndex(Matrix matrix) {
        int maxIndex = 0;
        for (int i = 0; i < matrix.getItemsNumber(); i++) {
            maxIndex = matrix.getItem(0, i) > matrix.getItem(0, maxIndex) ? i : maxIndex;
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
        return switch (errors.size()) {
            case 1 -> 0.0;
            case 2 -> getSpeed2(errors);
            case 3 -> getSpeed3(errors);
            case 4 -> getSpeed4(errors);
            case 5 -> getSpeed5(errors);
            default -> getSpeed6(errors);
        };
    }

    private double getSpeed2(List<Double> errors) {
        return errors.get(1) - errors.get(0);
    }

    private double getSpeed3(List<Double> errors) {
        double speed = 0;
        speed += +3 * getValue(errors, 0);
        speed += -4 * getValue(errors, -1);
        speed += +1 * getValue(errors, -2);
        return speed / 2;
    }

    private double getSpeed4(List<Double> errors) {
        double speed = 0;
        speed += +11 * getValue(errors, 0);
        speed += -18 * getValue(errors, -1);
        speed += +9 * getValue(errors, -2);
        speed += -2 * getValue(errors, -3);
        return speed / 6;
    }

    private double getSpeed5(List<Double> errors) {
        double speed = 0;
        speed += +25 * getValue(errors, 0);
        speed += -48 * getValue(errors, -1);
        speed += +36 * getValue(errors, -2);
        speed += -16 * getValue(errors, -3);
        speed += +3 * getValue(errors, -4);
        return speed / 12;
    }

    private double getSpeed6(List<Double> errors) {
        double speed = 0;
        speed += +137 * getValue(errors, 0);
        speed += -300 * getValue(errors, -1);
        speed += +300 * getValue(errors, -2);
        speed += -200 * getValue(errors, -3);
        speed += +75 * getValue(errors, -4);
        speed += -12 * getValue(errors, -5);
        return speed / 60;
    }


    private double getValue(List<Double> errors, int index) {
        return errors.get(errors.size() - 1 + index);
    }

}
