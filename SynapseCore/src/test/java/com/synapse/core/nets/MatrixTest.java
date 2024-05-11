package com.synapse.core.nets;

import com.synapse.core.matrix.Matrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.DoubleFunction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MatrixTest {

    static int row1, row2, row3;
    static int col1, col2, col3;
    static double[] array1, array2, array3;
    static Matrix matrix1, matrix2, matrix3;

    @BeforeEach
    void setUp() {
        row1 = 2; col1 = 3;
        row2 = 3; col2 = 2;
        row3 = 4; col3 = 3;

        array1 = new double[]{1, 2, 3, 4, 5, 6};
        array2 = new double[]{6, 5, 4, 3, 2, 1};
        array3 = new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3};

        matrix1 = Matrix.create(row1, col1, array1);
        matrix2 = Matrix.create(row2, col2, array2);
        matrix3 = Matrix.create(row3, col3, array3);
    }

    @Test
    void getItem() {
        assertEquals(array1[0], matrix1.getItem(0, 0));
        assertEquals(array1[col1], matrix1.getItem(1, 0));
        assertEquals(array1[col1 + 2], matrix1.getItem(1, 2));
    }

    @Test
    void getColLength() {
        assertEquals(col1, matrix1.getColumnLength());
        assertEquals(col2, matrix2.getColumnLength());
        assertEquals(col3, matrix3.getColumnLength());
    }

    @Test
    void getRowLength() {
        assertEquals(row1, matrix1.getRowLength());
        assertEquals(row2, matrix2.getRowLength());
        assertEquals(row3, matrix3.getRowLength());
    }

    @Test
    void add() {
        Matrix result = matrix1.add(matrix1);
        for (int i = 0; i < result.getRowLength(); i++) {
            for (int j = 0; j < result.getColumnLength(); j++) {
                assertEquals(result.getItem(i, j), matrix1.getItem(i, j) + matrix1.getItem(i, j));
            }
        }
    }

    @Test
    void sub() {
        Matrix result = matrix1.sub(matrix1);
        for (int i = 0; i < result.getRowLength(); i++) {
            for (int j = 0; j < result.getColumnLength(); j++) {
                assertEquals(result.getItem(i, j), matrix1.getItem(i, j) - matrix1.getItem(i, j));
            }
        }
    }

    @Test
    void prod() {
        Matrix result = matrix1.prod(matrix1);
        for (int i = 0; i < result.getRowLength(); i++) {
            for (int j = 0; j < result.getColumnLength(); j++) {
                assertEquals(result.getItem(i, j), matrix1.getItem(i, j) * matrix1.getItem(i, j));
            }
        }
    }

    @Test
    void mul() {
        Matrix result = matrix1.mul(matrix2);
        assertArrayEquals(new double[]{20, 14, 56, 41}, result.getArray());
    }

    @Test
    void tMul() {
        assertEquals(matrix1.mul(matrix2), matrix1.T().tMul(matrix2));
    }

    @Test
    void mulT() {
        assertEquals(matrix1.mul(matrix3.T()), matrix1.mulT(matrix3));
    }

    @Test
    void T() {
        Matrix result = matrix1.T();
        for (int i = 0; i < result.getRowLength(); i++) {
            for (int j = 0; j < result.getColumnLength(); j++) {
                assertEquals(result.getItem(i, j), matrix1.getItem(j, i));
            }
        }
    }

    @Test
    void normalize() {
        Matrix m = matrix1.clone();
        double norm = 0;
        for (var n : m) {
            norm += n * n;
        }
        norm = Math.sqrt(norm);
        m = m.normalize();

        for (int i = 0; i < m.getRowLength(); i++) {
            for (int j = 0; j < m.getColumnLength(); j++) {
                assertEquals(m.getItem(i, j), matrix1.getItem(i, j) / norm, 1e-9);
            }
        }
    }

    @Test
    void scale() {
        int k = 3;
        Matrix result = matrix1.scale(k);
        for (int i = 0; i < result.getRowLength(); i++) {
            for (int j = 0; j < result.getColumnLength(); j++) {
                assertEquals(result.getItem(i, j), matrix1.getItem(i, j) * k);
            }
        }
    }

    @Test
    void apply() {
        DoubleFunction<Double> f = Math::sin;
        Matrix result = matrix1.apply(f);
        for (int i = 0; i < result.getRowLength(); i++) {
            for (int j = 0; j < result.getColumnLength(); j++) {
                assertEquals(result.getItem(i, j), f.apply(matrix1.getItem(i, j)));
            }
        }
    }

    @Test
    void norm() {

        double norm = 0;
        for (var n : matrix1) {
            norm += n * n;
        }
        norm = Math.sqrt(norm);

        assertEquals(norm, matrix1.norm());

    }

    @Test
    void sum() {
        assertEquals(Arrays.stream(array1).sum(), matrix1.sum());
        assertEquals(Arrays.stream(array2).sum(), matrix2.sum());
        assertEquals(Arrays.stream(array3).sum(), matrix3.sum());
    }

    @Test
    void average() {
        assertEquals(Arrays.stream(array1).average().getAsDouble(), matrix1.average());
        assertEquals(Arrays.stream(array2).average().getAsDouble(), matrix2.average());
        assertEquals(Arrays.stream(array3).average().getAsDouble(), matrix3.average());
    }

    @Test
    void zeros() {
        Matrix m = matrix1.clone();
        Matrix.zeros(m);
        for (var n : m) {
            assertEquals(0.0, n);
        }
    }

}