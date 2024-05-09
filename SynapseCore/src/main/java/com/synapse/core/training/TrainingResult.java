package com.synapse.core.training;

import com.synapse.core.nets.Net;
import com.synapse.core.tools.Reportable;
import lombok.*;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Data
@NoArgsConstructor
public class TrainingResult implements Externalizable, Reportable {
    @Serial
    private static final long serialVersionUID = -343254775546L;

    @Getter
    @Setter
    private List<Double> testingPercents = new ArrayList<>();
    @Getter
    @Setter
    private List<Double> testingErrors = new ArrayList<>();
    @Getter
    @Setter
    private List<Double> trainingErrors = new ArrayList<>();
    @Getter
    @Setter
    private int epochCount = 0;
    @Getter
    @Setter
    private StopReason stopReason;
    @Getter
    @Setter
    private Duration duration = null;
    @Getter
    @Setter
    private Net bestNet = null;

    public DoubleSummaryStatistics getTrainingErrorsStatistics() {
        return trainingErrors.stream().mapToDouble(Double::valueOf).summaryStatistics();
    }

    public DoubleSummaryStatistics getTestingErrorsStatistics() {
        return testingErrors.stream().mapToDouble(Double::valueOf).summaryStatistics();
    }

    public DoubleSummaryStatistics getTestingPercentsStatistics() {
        return testingPercents.stream().mapToDouble(Double::valueOf).summaryStatistics();
    }

    @Override
    public List<String> getReport() {
        return List.of(
                "TrainingResult:\n",
                "\tepochCount=%s\n".formatted(epochCount),
                "\tstop reason=%s\n".formatted(stopReason.getDescription()),
                "\tmin training error=%.3f\n".formatted(getTrainingErrorsStatistics().getMin()),
                "\tmid training error=%.3f\n".formatted(getTrainingErrorsStatistics().getAverage()),
                "\tmax training error=%.3f\n".formatted(getTrainingErrorsStatistics().getMax()),
                "\tmin testing error=%.3f\n".formatted(getTestingErrorsStatistics().getMin()),
                "\tmid testing error=%.3f\n".formatted(getTestingErrorsStatistics().getAverage()),
                "\tmax testing error=%.3f\n".formatted(getTestingErrorsStatistics().getMax()),
                "\tmin testing percent=%.3f%%\n".formatted(getTestingPercentsStatistics().getMin()),
                "\tmid testing percent=%.3f%%\n".formatted(getTestingPercentsStatistics().getAverage()),
                "\tmax testing percent=%.3f%%\n".formatted(getTestingPercentsStatistics().getMax()),
                "\tduration=%s\n".formatted(stringTime(duration.toSeconds()))
                );
    }

    private String stringTime(long seconds) {
        return "%02d ч. %02d м. %02d с.".formatted(
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60
        );
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(duration.toNanos());
        out.writeInt(epochCount);
        out.writeObject(bestNet);
        out.writeObject(stopReason);

        out.writeInt(trainingErrors.size());
        for (Double learnError : trainingErrors) {
            out.writeDouble(learnError);
        }

        out.writeInt(testingErrors.size());
        for (Double testError : testingErrors) {
            out.writeDouble(testError);
        }

        out.writeInt(testingPercents.size());
        for (Double testError : testingPercents) {
            out.writeDouble(testError);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        duration = Duration.ofNanos(in.readLong());
        epochCount = in.readInt();
        bestNet = (Net) in.readObject();
        stopReason = (StopReason) in.readObject();

        int length = in.readInt();
        for (int i = 0; i < length; i++) {
            trainingErrors.add(in.readDouble());
        }

        length = in.readInt();
        for (int i = 0; i < length; i++) {
            testingErrors.add(in.readDouble());
        }

        length = in.readInt();
        for (int i = 0; i < length; i++) {
            testingPercents.add(in.readDouble());
        }
    }

    public List<Double> getAverageTrainingErrors() {
        List<Double> values = new ArrayList<>();
        int k = 50;
        for (int i = 0; i < trainingErrors.size(); i++) {
            double value = 0;
            for (int j = -k / 2; j <= k / 2; j++) {
                value += getValue(trainingErrors, i + j);
            }
            values.add(value / k);
        }
        return values;
    }

    private double getValue(List<Double> trainingErrors, int index) {
        if (index < 0) return trainingErrors.get(0);
        if (index >= trainingErrors.size()) return trainingErrors.get(trainingErrors.size() - 1);
        return trainingErrors.get(index);
    }


    public enum StopReason {
        MAX_EPOCH("Достигнуто максимальное количество эпох обучения."),
        MIN_ERROR("Достигнута минимальная ошибка обучения."),
        OVERFITTING("Переобучение: ошибка обучения перестала убывать.");

        final String description;

        StopReason(String s) {
            description = s;
        }

        public String getDescription() {
            return description;
        }
    }
}
