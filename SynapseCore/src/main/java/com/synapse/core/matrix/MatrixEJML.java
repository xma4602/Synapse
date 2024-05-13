package com.synapse.core.matrix;

import lombok.NoArgsConstructor;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.util.Iterator;
import java.util.function.DoubleFunction;


@NoArgsConstructor
public class MatrixEJML implements Matrix {


    @Serial
    private static final long serialVersionUID = -6776373323609919141L;

    private SimpleMatrix simpleMatrix;

    public MatrixEJML(SimpleMatrix simpleMatrix) {
        this.simpleMatrix = simpleMatrix;
    }

    public MatrixEJML(int rows, int columns, double... data) {
        simpleMatrix = new SimpleMatrix(rows, columns, true, data);
    }

    @Override
    public Matrix createInstance(int rows, int columns, double... data) {
        return new MatrixEJML(rows, columns, data);
    }

    @Override
    public int getRowsNumber() {
        return simpleMatrix.getMatrix().getNumRows();
    }

    @Override
    public int getColumnsNumber() {
        return simpleMatrix.getMatrix().getNumCols();
    }

    @Override
    public double getItem(int row, int column) {
        return simpleMatrix.get(row, column);
    }

    @Override
    public double[] getArray() {
        return simpleMatrix.getDDRM().getData();
    }

    @Override
    public Matrix add(Matrix m) {
        MatrixEJML matr = (MatrixEJML) m;
        return new MatrixEJML(simpleMatrix.plus(matr.simpleMatrix));
    }


    @Override
    public Matrix sub(Matrix m) {
        MatrixEJML matr = (MatrixEJML) m;
        return new MatrixEJML(simpleMatrix.minus(matr.simpleMatrix));
    }

    @Override
    public Matrix prod(Matrix m) {
        MatrixEJML matr = (MatrixEJML) m;
        return new MatrixEJML(simpleMatrix.elementMult(matr.simpleMatrix));
    }

    @Override
    public Matrix mul(Matrix m) {
        MatrixEJML matr = (MatrixEJML) m;
        return new MatrixEJML(simpleMatrix.mult(matr.simpleMatrix));
    }

    @Override
    public Matrix tMul(Matrix m) {
        MatrixEJML matr = (MatrixEJML) m;
        return new MatrixEJML(simpleMatrix.transpose().mult(matr.simpleMatrix));
    }

    @Override
    public Matrix mulT(Matrix m) {
        MatrixEJML matr = (MatrixEJML) m;
        return new MatrixEJML(simpleMatrix.mult(matr.simpleMatrix.transpose()));
    }

    @Override
    public Matrix Trans() {
        return new MatrixEJML(simpleMatrix.transpose());
    }

    @Override
    public Matrix scale(double scalar) {
        return new MatrixEJML(simpleMatrix.scale(scalar));
    }

    @Override
    public Matrix scaleAdd(double scale, Matrix matrix) {
        MatrixEJML matr = (MatrixEJML) matrix;
        return new MatrixEJML(simpleMatrix.plus(scale, matr.simpleMatrix));
    }

    @Override
    public Matrix apply(DoubleFunction<Double> function) {

        var matrix = simpleMatrix.copy().getDDRM();
        for (int i = 0; i < matrix.getNumElements(); i++)
            matrix.set(i, function.apply(matrix.get(i)));

        return new MatrixEJML(new SimpleMatrix(matrix));
    }

    @Override
    public Matrix normalize() {
        return new MatrixEJML(simpleMatrix.copy().divide(simpleMatrix.normF()));
    }

    @Override
    public double norm2() {
        return simpleMatrix.normF();
    }

    @Override
    public double sum() {
        return simpleMatrix.elementSum();
    }

    @Override
    public double sqrsSum() {
        return simpleMatrix.dot(simpleMatrix);
    }

    @Override
    public double average() {
        return simpleMatrix.elementSum() / simpleMatrix.getNumElements();
    }

    @Override
    public void zeros() {
        simpleMatrix.zero();
    }

    @Override
    public Matrix clone() {
        return new MatrixEJML(simpleMatrix.copy());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(simpleMatrix.getNumRows());
        out.writeInt(simpleMatrix.getNumCols());
        for (double datum : simpleMatrix.getDDRM().getData()) {
            out.writeDouble(datum);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int rows = in.readInt();
        int columns = in.readInt();
        double[] data = new double[rows * columns];
        for (int i = 0; i < data.length; i++) {
            data[i] = in.readDouble();
        }
        simpleMatrix = new SimpleMatrix(rows, columns, true, data);
    }

    @Override
    public Iterator<Double> iterator() {
        return simpleMatrix.iterator(true, 0, 0, simpleMatrix.getNumRows(), simpleMatrix.getNumCols());
    }
    @Override
    public String toString() {
        return "Matrix{%dx%d}".formatted(getRowsNumber(), getColumnsNumber());
    }

}
