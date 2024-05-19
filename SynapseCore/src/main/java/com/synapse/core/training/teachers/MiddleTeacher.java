package com.synapse.core.training.teachers;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.matrix.MatrixUtils;
import com.synapse.core.nets.Net;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.Sample;
import com.synapse.core.samples.SampleBatches;
import com.synapse.core.tools.Monitored;
import com.synapse.core.tools.Timing;
import com.synapse.core.training.testers.ParallelTester;
import com.synapse.core.training.testers.Tester;
import com.synapse.core.training.TrainingResult;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleFunction;

import static com.synapse.core.tools.DelayedFormatter.format;

@Slf4j
@NoArgsConstructor
public class MiddleTeacher extends Teacher {

    private Matrix[] correctionsW;
    private Matrix[] correctionsB;
    private Net net;
    private Rate rateFunc;
    private int epochCount;
    private Tester tester;
    private List<Double> trainingErrors;
    private CalcContext context;

    @Override
    public void learn() {
        log.info("{} | LEARNING:   STARTED", teacherName);
        resetVariables();
        testing(); //тестирование работы сети перед обучением

        epochCount = 0;
        Timing timing = Timing.startTiming();
        do { //цикл одной эпохи
            log.debug("{} | TRAINING:   STARTED: epoch={}", teacherName, epochCount + 1);

            Iterable<Iterable<Sample>> batches = getNewBatches();
            int batchCount = 0;

            for (Iterable<Sample> batch : batches) { //цикл по пакетам
                MatrixUtils.zeros(correctionsW); //зануление накопителей корректировок
                MatrixUtils.zeros(correctionsB); //зануление накопителей корректировок
                int sampleCount = 0;

                double batchError = 0; //аккумулятор ошибки пакета
                for (Sample sample : batch) { //цикл одного пакета
                    context.pass(sample, batchCount, sampleCount); //проход вперед и назад по сети
                    batchError += context.getErrorValue(); //сложение ошибки пакета
                    addCorrections(context.getCorrectionsW(), context.getCorrectionsB()); //сложение корректировки прохода с корректировкой пакета
                    sampleCount++;
                }
                batchError /= trainingParameters.getBatchSize();
                trainingErrors.add(batchError); //сохранение ошибки пакета
                applyCorrections(); //коррекция весов
                batchCount++;
            }

            epochCount++; //увеличение счетчика эпох
            testing();
        }
        while (tester.isContinue()); //проверка условия остановки
        log.debug("{} | TRAINING: COMPLETED: epoch={}", teacherName, epochCount);

        trainingResult.setDuration(timing.stopTiming());
        trainingResult.setEpochCount(epochCount);
        trainingResult.setStopReason(tester.getStopReason());
        trainingResult.setBestNet(tester.getBestNet());
        trainingResult.setTrainingErrors(trainingErrors);
        trainingResult.setTestingErrors(tester.getTestingErrors());
        trainingResult.setTestingPercents(tester.getTestingPercents());
        log.info("{} | LEARNING: COMPLETED", teacherName);
    }

    private void resetVariables() {
        net = netParameters.createNet();
        rateFunc = trainingParameters.getRate();
        trainingErrors = new ArrayList<>();
        trainingResult = new TrainingResult();

        tester = new ParallelTester();
        tester.setName(teacherName);
        tester.setTrainingParameters(trainingParameters);

        int layerCount = net.getInterLayersCount();
        Matrix[] weights = net.getWeights();
        Matrix[] biases = net.getBiases();

        context = new CalcContext(layerCount, weights, biases);

        correctionsW = new Matrix[layerCount];
        correctionsB = new Matrix[layerCount];
        for (int i = 0; i < layerCount; i++) {
            correctionsW[i] = Matrix.create(weights[i].getRowsNumber(), weights[i].getColumnsNumber());
            correctionsB[i] = Matrix.create(biases[i].getRowsNumber(), biases[i].getColumnsNumber());
        }
    }

    private class CalcContext {
        private Matrix[] y;
        private Matrix[] v;
        private Matrix[] g;
        private Matrix[] w;
        private Matrix[] b;
        private Matrix errorMatrix;

        private double errorValue;

        public CalcContext(int layerCount, Matrix[] weights, Matrix[] biases) {
            y = new Matrix[layerCount + 1];
            v = new Matrix[layerCount];
            g = new Matrix[layerCount];
            w = new Matrix[layerCount];
            b = new Matrix[layerCount];

            y[0] = Matrix.create(weights[0].getRowsNumber());
            for (int i = 0; i < weights.length; i++) {
                y[i + 1] = Matrix.create(weights[i].getColumnsNumber());
                v[i] = Matrix.create(weights[i].getColumnsNumber());
                g[i] = Matrix.create(weights[i].getColumnsNumber());
                w[i] = Matrix.create(weights[i].getRowsNumber(), weights[i].getColumnsNumber());
                b[i] = Matrix.create(biases[i].getColumnsNumber());
            }
            errorMatrix = Matrix.create(weights[weights.length - 1].getColumnsNumber());
        }

        public void pass(Sample sample, int batchCount, int sampleCount) {
            forwardPass(sample.getSource()); //прямой проход сети
            calcError(sample.getTarget()); //вычисление и сохранение ошибки сети
            backwardPass(); //обратный проход

            log.trace("{} | TRAINING: epoch={}, batch={}, sample={}, error={}",
                    teacherName,
                    format("%03d", epochCount + 1),
                    format("%03d", batchCount + 1),
                    format("%02d", sampleCount + 1),
                    format("%.4f", errorValue)
            );
        }


        private void forwardPass(Matrix input) {
            DoubleFunction<Double> f = net.getActivator();
            Matrix[] weights = net.getWeights();
            Matrix[] biases = net.getBiases();

            y[0] = input; // Y[0] = X
            for (int i = 0; i < net.getInterLayersCount(); i++) {
                // V[i] = Y[i] x W[i] + B[i]
                MatrixUtils.mul(y[i], weights[i], v[i]);
                MatrixUtils.add(v[i], biases[i], v[i]);
                // Y[i+1] = f(V[i])
                MatrixUtils.apply(v[i], f, y[i + 1]);
            }
        }

        private void calcError(Matrix target) {
            Matrix output = y[y.length - 1]; // O = Y[L-1]
            MatrixUtils.sub(target, output, errorMatrix); // E = T - O
            errorValue = errorMatrix.sqrsSum() / 2;
        }

        private void backwardPass() {
            DoubleFunction<Double> df = net.getDeactivator();
            Matrix[] weights = net.getWeights();
            int last = w.length - 1;

            // δ[L-1] = -f'(V[L-1]) * E
            MatrixUtils.apply(v[last], df, g[last]);
            MatrixUtils.prod(g[last], errorMatrix, g[last]);
            MatrixUtils.scale(g[last], -1.0, g[last]);

            // ΔW[L-1] = Y[L-1]^T x δ[L-1]
            MatrixUtils.tMul(y[last], g[last], w[last]);
            // ΔB[L-1] = δ[L-1]
            MatrixUtils.copyTo(g[last], b[last]);

            for (int i = last - 1; i >= 0; i--) {
                // δ[i] = f'(V[i]) * (δ[i+1] x W[i+1]^T)
                MatrixUtils.apply(v[i], df, v[i]);
                MatrixUtils.mulT(g[i + 1], weights[i + 1], g[i]);
                MatrixUtils.prod(v[i], g[i], g[i]);

                // ΔW[i] = Y[i]^T x δ[i]
                MatrixUtils.tMul(y[i], g[i], w[i]);
                // ΔB[i] = δ[i]
                MatrixUtils.copyTo(g[i], b[i]);
            }
        }

        public Matrix[] getCorrectionsW() {
            return w;
        }

        public Matrix[] getCorrectionsB() {
            return b;
        }


        public double getErrorValue() {
            return errorValue;
        }
    }

    private Iterable<Iterable<Sample>> getNewBatches() {
        return new SampleBatches(
                trainingParameters.getBatchSize(),
                sampleService.getTrainingSampling()
        );
    }


    private void addCorrections(Matrix[] dw, Matrix[] db) {
        for (int i = 0; i < correctionsW.length; i++) {
            MatrixUtils.add(correctionsW[i], dw[i], correctionsW[i]);
            MatrixUtils.add(correctionsB[i], db[i], correctionsB[i]);
        }
    }


    private void applyCorrections() {
        Matrix[] weights = net.getWeights();
        Matrix[] biases = net.getBiases();
        double rate = rateFunc.apply(epochCount); //получение скорости обучения на данной эпохе

        for (int i = 0; i < net.getInterLayersCount(); i++) {
            // W[i] = W[i] + (-η * ΔW[i])
            MatrixUtils.scaleSub(weights[i], correctionsW[i], rate, weights[i]);
            // B[i] = B[i] + (-η * ΔB[i])
            MatrixUtils.scaleSub(biases[i], correctionsB[i], rate, biases[i]);
        }
    }

    private void testing() {
        tester.test(net, sampleService.getTestingSampling(), epochCount);
    }

    @Override
    public double getProgress() {
        if (tester.isContinue()) {
            return epochCount * 1.0 / getTrainingParameters().getMaxEpochsCount();
        } else return 1.0;
    }

    @Override
    public Iterable<Monitored> getProcessComponents() {
        return Collections.emptyList();
    }
}
