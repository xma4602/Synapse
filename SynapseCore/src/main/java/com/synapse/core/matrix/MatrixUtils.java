package com.synapse.core.matrix;

import java.util.function.DoubleFunction;

/**
 * Класс двумерного массива (матрицы) для алгебраических вычислений
 */
public class MatrixUtils {

    private static void validateColumnsMismatch(Matrix left, Matrix right) {
        if (left.getColumnsNumber() != right.getColumnsNumber())
            throw new ArithmeticException("Не совпадает количество столбцов: %s и %s"
                    .formatted(left.getColumnsNumber(), right.getColumnsNumber()));
    }

    private static void validateRowsMismatch(Matrix left, Matrix right) {
        if (left.getRowsNumber() != right.getRowsNumber())

            throw new ArithmeticException("Не совпадает количество строк: %s и %s"
                    .formatted(left.getRowsNumber(), right.getRowsNumber()));
    }

    private static void validateColumnsRowsMismatch(Matrix left, Matrix right) {
        if (left.getColumnsNumber() != right.getRowsNumber())
            throw new ArithmeticException("Не совпадает количество столбцов левой матрицы и строк правой матрицы: %s и %s"
                    .formatted(left.getColumnsNumber(), right.getRowsNumber()));
    }

    private static void validateRowsColumnsMismatch(Matrix left, Matrix right) {
        validateColumnsRowsMismatch(right, left);
    }

    public static void add(Matrix a, Matrix b, Matrix c) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        validateRowsMismatch(a, c);
        validateColumnsMismatch(a, c);

        for (int i = 0; i < a.getItemsNumber(); i++)
            c.setItem(i, a.getItem(i) + b.getItem(i));
    }

    public static void sub(Matrix a, Matrix b, Matrix c) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        validateRowsMismatch(a, c);
        validateColumnsMismatch(a, c);

        for (int i = 0; i < a.getItemsNumber(); i++)
            c.setItem(i, a.getItem(i) - b.getItem(i));
    }

    public static void prod(Matrix a, Matrix b, Matrix c) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        validateRowsMismatch(a, c);
        validateColumnsMismatch(a, c);

        for (int i = 0; i < a.getItemsNumber(); i++)
            c.setItem(i, a.getItem(i) * b.getItem(i));
    }

    public static void mul(Matrix a, Matrix b, Matrix c) {
        validateRowsMismatch(a, c);
        validateColumnsMismatch(b, c);
        validateColumnsRowsMismatch(a, b);

        int r1 = c.getRowsNumber();
        int c1 = c.getColumnsNumber();
        int c2 = a.getColumnsNumber();

        for (int i = 0; i < r1; i++)
            for (int j = 0; j < c1; j++) {
                double value = 0.0;
                for (int k = 0; k < c2; k++) {
                    value += a.getItem(i, k) * b.getItem(k, j);
                }
                c.setItem(i, j, value);
            }

    }

    public static void tMul(Matrix a, Matrix b, Matrix c) {
        validateRowsMismatch(a, b);
        validateRowsColumnsMismatch(c, a);
        validateColumnsMismatch(c, b);

        int r1 = c.getRowsNumber();
        int c1 = c.getColumnsNumber();
        int c2 = a.getRowsNumber();

        for (int i = 0; i < r1; i++)
            for (int j = 0; j < c1; j++) {
                double value = 0.0;
                for (int k = 0; k < c2; k++) {
                    value += a.getItem(k, i) * b.getItem(k, j);
                }
                c.setItem(i, j, value);
            }

    }

    public static void mulT(Matrix a, Matrix b, Matrix c) {
        validateColumnsMismatch(a, b);
        validateRowsMismatch(c, a);
        validateColumnsRowsMismatch(c, b);

        int r1 = c.getRowsNumber();
        int c1 = c.getColumnsNumber();
        int c2 = a.getColumnsNumber();

        for (int i = 0; i < r1; i++)
            for (int j = 0; j < c1; j++) {
                double value = 0.0;
                for (int k = 0; k < c2; k++) {
                    value += a.getItem(i, k) * b.getItem(j, k);
                }
                c.setItem(i, j, value);
            }

    }

    public static void trans(Matrix a, Matrix b) {
        validateColumnsMismatch(a, b);
        validateColumnsRowsMismatch(a, b);

        for (int i = 0; i < a.getRowsNumber(); i++)
            for (int j = 0; j < a.getColumnsNumber(); j++)
                b.setItem(j, i, a.getItem(i, j));
    }

    public static void scale(Matrix a, double scalar, Matrix b) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        for (int i = 0; i < a.getItemsNumber(); i++) {
            b.setItem(i, a.getItem(i) * scalar);
        }
    }

    public static void scaleAdd(Matrix a, Matrix b, double scalar, Matrix c) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        for (int i = 0; i < a.getItemsNumber(); i++) {
            c.setItem(i, a.getItem(i) + scalar * b.getItem(i));
        }
    }

    public static void scaleSub(Matrix a, Matrix b, double scalar, Matrix c) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        for (int i = 0; i < a.getItemsNumber(); i++) {
            c.setItem(i, a.getItem(i) - scalar * b.getItem(i));
        }
    }


    public static void apply(Matrix a, DoubleFunction<Double> function, Matrix b) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        for (int i = 0; i < a.getItemsNumber(); i++) {
            b.setItem(i, function.apply(a.getItem(i)));
        }
    }

    public static void copyTo(Matrix a, Matrix b) {
        validateRowsMismatch(a, b);
        validateColumnsMismatch(a, b);
        for (int i = 0; i < a.getItemsNumber(); i++) {
            b.setItem(i, a.getItem(i));
        }
    }

    public static void zeros(Matrix... matrices) {
        for (Matrix matrix : matrices) {
            matrix.zeros();
        }
    }

    public static String toString(Matrix matrix) {
        double[] array = matrix.getArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            builder.append("%+.4f,".formatted(array[i]));
        }
        builder.append("%+.4f".formatted(array[array.length - 1]));
        return "Matrix{%dx%d}[%s]".formatted(matrix.getRowsNumber(), matrix.getColumnsNumber(), builder);
    }

}
