package com.synapse.core.matrix;

public class MatrixSettings {

    public static Matrix getMatrixClass() {
        return matrixClass;
    }

    public static void setMatrixClass(Matrix matrix) {
        matrixClass = matrix;
    }

    private static Matrix matrixClass = new MatrixJava();
}
