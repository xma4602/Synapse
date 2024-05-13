package com.synapse.core.training;

import com.synapse.core.matrix.Matrix;
import com.synapse.core.nets.Net;
import com.synapse.core.rates.Rate;
import com.synapse.core.samples.Sample;
import com.synapse.core.samples.SampleBatches;
import com.synapse.core.tools.Monitored;
import com.synapse.core.tools.Timing;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleFunction;

import static com.synapse.core.tools.DelayedFormatter.format;

@Slf4j
public class SimpleTeacher extends Teacher {

    //временные переменные обучения
    private Matrix[] y;
    private Matrix[] v;
    private Matrix[] g;
    private Matrix[] w;
    private Matrix[] b;
    /**
     * Накопитель корректировок (дельта-W).
     * В нем складываются матрицы-корректировки для матриц весов каждого слоя сети в течение одного пакета обучения.
     */
    private Matrix[] dw;
    /**
     * Накопитель корректировок (дельта-B).
     * В нем складываются матрицы-корректировки для матриц смещений каждого слоя сети в течение одного пакета обучения.
     */
    private Matrix[] db;

    Net net;
    Rate rateFunc;
    int epochCount;
    Tester tester;
    List<Double> trainingErrors;

    @Override
    public void learn() {
        log.info("{} / LEARNING:   STARTED", teacherName);
        resetVariables();
        testing(); //тестирование работы сети перед обучением

        epochCount = 0;
        Timing timing = Timing.startTiming();
        do { //цикл одной эпохи
            log.debug("{} / TRAINING:   STARTED: epoch={}", teacherName, epochCount + 1);

            Iterable<Iterable<Sample>> batches = getNewBatches();
            int batchCount = 1;

            for (Iterable<Sample> batch : batches) { //цикл по пакетам
                Matrix.zeros(dw); //зануление накопителей корректировок
                Matrix.zeros(db); //зануление накопителей корректировок
                int sampleCount = 1;

                for (Sample sample : batch) { //цикл одного пакета
                    Matrix output = forwardPass(net, sample.getSource()); //прямой проход сети
                    Matrix trainingError = sample.getTarget().sub(output); //вычисление ошибки сети
                    double error = calcTrainingError(trainingError); //сохранение тренировочной ошибки
                    Matrix[][] corrects = backwardPass(net, trainingError); //обратный проход
                    addCorrections(corrects); //сложение корректировки прохода с корректировкой пакета
                    log.trace("{} / TRAINING: epoch={}, batch={}, sample={}, error={}",
                            teacherName,
                            format("%03d", epochCount + 1),
                            format("%05d", batchCount),
                            format("%05d", sampleCount),
                            format("%.8f", error));
                    sampleCount++;
                }

                double rate = rateFunc.apply(epochCount); //получение скорости обучения на данной эпохе
                applyCorrections(net, dw, db, rate); //коррекция весов
                batchCount++;
            }

            System.gc(); //Попытка защититься от OutOfMemoryError
            epochCount++; //увеличение счетчика эпох
            testing();
            System.gc(); //Попытка защититься от OutOfMemoryError
        }
        while (tester.isContinue()); //проверка условия остановки
        log.debug("{} / TRAINING: COMPLETED: epoch={}", teacherName, epochCount);

        trainingResult.setDuration(timing.stopTiming());
        trainingResult.setEpochCount(epochCount);
        trainingResult.setStopReason(tester.getStopReason());
        trainingResult.setBestNet(tester.getBestNet());
        trainingResult.setTrainingErrors(trainingErrors);
        trainingResult.setTestingErrors(tester.getTestingErrors());
        trainingResult.setTestingPercents(tester.getTestingPercents());
        log.info("{} / LEARNING: COMPLETED", teacherName);
    }

    private Iterable<Iterable<Sample>> getNewBatches() {
        return new SampleBatches(
                trainingParameters.getBatchSize(),
                sampleService.getTrainingSampling()
        );
    }

    private void resetVariables() {
        net = netParameters.createNet();
        rateFunc = trainingParameters.getRate();
        tester = new Tester(teacherName, trainingParameters);
        trainingErrors = new ArrayList<>();
        trainingResult = new TrainingResult();

        int layerCount = net.getInterLayersCount();
        y = new Matrix[layerCount + 1];
        v = new Matrix[layerCount];
        g = new Matrix[layerCount];
        w = new Matrix[layerCount];
        b = new Matrix[layerCount];
        dw = new Matrix[layerCount];
        db = new Matrix[layerCount];

        for (int i = 0; i < layerCount; i++) {
            dw[i] = net.getWeights()[i].clone();
            db[i] = net.getBiases()[i].clone();
        }
    }

    private double calcTrainingError(Matrix trainingError) {
        double error = trainingError.sqrsSum() / 2;
        trainingErrors.add(error);
        return error;
    }

    private void addCorrections(Matrix[][] corrections) {
        for (int i = 0; i < dw.length; i++) {
            dw[i] = dw[i].add(corrections[0][i]);
            db[i] = db[i].add(corrections[1][i]);
        }
    }

    private Matrix forwardPass(Net net, Matrix input) {
        DoubleFunction<Double>[] f = net.getActivators();
        Matrix[] weights = net.getWeights();
        Matrix[] biases = net.getBiases();

        y[0] = input;
        for (int i = 0; i < net.getInterLayersCount(); i++) {
            v[i] = y[i].mul(weights[i]).add(biases[i]);
            y[i + 1] = v[i].apply(f[i]);
        }
        return y[y.length - 1];
    }

    private Matrix[][] backwardPass(Net net, Matrix error) {
        DoubleFunction<Double>[] df = net.getDeactivates();
        Matrix[] weights = net.getWeights();

        int last = w.length - 1;

        g[last] = v[last].apply(df[last]).prod(error).scale(-1);
        w[last] = y[last].tMul(g[last]);
        b[last] = g[last];

        for (int i = last - 1; i >= 0; i--) {
            g[i] = g[i + 1].mulT(weights[i + 1]).prod(v[i].apply(df[i]));
            w[i] = y[i].tMul(g[i]);
            b[i] = g[i];
        }

        return new Matrix[][]{w, b};
    }

    private void applyCorrections(Net net, Matrix[] dw, Matrix[] db, double rate) {
        Matrix[] weights = net.getWeights();
        Matrix[] biases = net.getBiases();

        for (int i = 0; i < net.getInterLayersCount(); i++) {
            weights[i] = weights[i].scaleAdd(-rate, dw[i]);
            biases[i] = biases[i].scaleAdd(-rate, db[i]);
//            weights[i] = weights[i].add(dw[i].scale(-rate));
//            biases[i] = biases[i].add(db[i].scale(-rate));
        }
    }

    private void testing() {
        tester.test(net, sampleService.getTestingSampling(), epochCount);
    }

    @Override
    public double getProgress() {
        if (tester.isContinue()) {
            return epochCount / (double) getTrainingParameters().getMaxEpochsCount();
        } else return 1.0;
    }

    @Override
    public Iterable<Monitored> getProcessComponents() {
        return Collections.emptyList();
    }
}
