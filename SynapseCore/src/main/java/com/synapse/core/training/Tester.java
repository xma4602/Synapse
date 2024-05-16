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

    public static final double OVERFITTING_LIMIT = 1.0;

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
            log.trace("{} /  TESTING: count={}, error={}, percent={}%",
                    name,
                    format("%04d", count),
                    format("%07.4f", error / count),
                    format("%08.4f", percent / count * 100)
            );
//            log.trace("{} /  TESTING: count={}, error={}, percent={}%, result={}, target={}",
//                    name,
//                    format("%04d", count),
//                    format("%07.4f", error / count),
//                    format("%08.4f", percent / count * 100),
//                    result, target
//            );
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

    public static int getClassIndex(Matrix matrix) {
        int maxIndex = 0;
        for (int i = 0; i < matrix.getItemNumber(); i++) {
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
