package com.synapse.core.matrix;

import lombok.NoArgsConstructor;
import org.ujmp.core.calculation.Calculation;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;
import org.ujmp.core.doublematrix.DoubleMatrix;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.function.DoubleFunction;
import java.util.stream.StreamSupport;

@NoArgsConstructor
public class MatrixUJMP implements Matrix {

    private DenseDoubleMatrix2D matrix2D;

    public MatrixUJMP(DenseDoubleMatrix2D matrix2D) {
        this.matrix2D = matrix2D;
    }

    @Override
    public Matrix createInstance(int rows, int columns, double... data) {
        DenseDoubleMatrix2D denseDoubleMatrix2D = DoubleMatrix.Factory.zeros(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                denseDoubleMatrix2D.setAsDouble(data[row * rows + columns]);
            }
        }
        return new MatrixUJMP(denseDoubleMatrix2D);
    }

    @Override
    public int getRowsNumber() {
        return (int) matrix2D.getRowCount();
    }

    @Override
    public int getColumnsNumber() {
        return (int) matrix2D.getColumnCount();
    }

    @Override
    public double getItem(int row, int column) {
        return matrix2D.getAsDouble(row, column);
    }

    @Override
    public double[] getArray() {
        double[] doubles = new double[(int) (matrix2D.getRowCount() * matrix2D.getColumnCount())];
        for (int row = 0; row < matrix2D.getColumnCount(); row++) {
            for (int col = 0; col < matrix2D.getColumnCount(); col++) {
                doubles[(int) (row * matrix2D.getColumnCount() + col)] = matrix2D.getAsDouble(row, col);
            }
        }
        return doubles;
    }

    @Override
    public Matrix add(Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.plus(matr));
    }

    @Override
    public Matrix sub(Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.minus(matr));
    }

    @Override
    public Matrix prod(Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.times(matr));
    }

    @Override
    public Matrix mul(Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.mtimes(matr));
    }

    @Override
    public Matrix tMul(Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.transpose().mtimes(matr));
    }

    @Override
    public Matrix mulT(Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.mtimes(matr.transpose()));
    }

    @Override
    public Matrix Trans() {
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.transpose());
    }

    @Override
    public Matrix scale(double scalar) {
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.times(scalar));
    }

    @Override
    public Matrix scaleAdd(double scale, Matrix matrix) {
        DenseDoubleMatrix2D matr = (DenseDoubleMatrix2D) matrix;
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.plus(matr.times(scale)));
    }

    @Override
    public Matrix apply(DoubleFunction<Double> function) {
        DenseDoubleMatrix2D array = matrix2D.Factory.zeros(matrix2D.getRowCount(), matrix2D.getColumnCount());
        for (int row = 0; row < matrix2D.getRowCount(); row++) {
            for (int col = 0; col < matrix2D.getColumnCount(); col++) {
                array.setAsDouble(function.apply(matrix2D.getAsDouble(row, col)), row, col);
            }
        }
        return new MatrixUJMP(array);
    }

    @Override
    public Matrix normalize() {
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.normalize(Calculation.Ret.NEW, 0));
    }

    @Override
    public double norm2() {
        return matrix2D.norm2();
    }

    @Override
    public double sum() {
        return matrix2D.getValueSum();
    }

    @Override
    public double sqrsSum() {
        return StreamSupport.stream(matrix2D.allValues().spliterator(), true)
                .mapToDouble(d -> (double) d)
                .map(d -> d * d)
                .sum();
    }

    @Override
    public double average() {
        return matrix2D.getMeanValue();
    }

    @Override
    public void zeros() {
        matrix2D.zeros(Calculation.Ret.ORIG);
    }

    @Override
    public Matrix clone() {
        return new MatrixUJMP((DenseDoubleMatrix2D) matrix2D.clone());
    }

    @Override
    public Iterator<Double> iterator() {
        return (Iterator<Double>) matrix2D.allValues();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(getRowsNumber());
        out.writeInt(getRowsNumber());
        Iterator<Double> objects = (Iterator<Double>) matrix2D.allValues();
        while (objects.hasNext()) {
            out.writeDouble(objects.next());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        int rowNum = in.readInt();
        int columnNum = in.readInt();
        matrix2D = matrix2D.Factory.zeros(rowNum, columnNum);
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < columnNum; col++) {
                matrix2D.setAsDouble(in.readDouble(), row, col);
            }
        }
    }

}
