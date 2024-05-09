package com.synapse.core.nets;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

/**
 * Класс двумерного массива (матрицы) для алгебраических вычислений
 */
@EqualsAndHashCode
@NoArgsConstructor
public class Matrix implements Cloneable, Iterable<Double>, Externalizable {

    @Serial
    private static final long serialVersionUID = 5679755609199712210L;
    /**
     * Массив данных матрицы
     */
    private double[] array;

    /**
     * Количество строк матрицы
     */
    @Getter
    private int rowLength;
    /**
     * Количество столбцов матрицы
     */
    @Getter
    private int columnLength;

    /**
     * Создает новую пустую матрицу с заданным количеством строк и столбцов
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @throws IllegalArgumentException если количество строк и/или столбцов не натуральное число
     */
    public Matrix(int rows, int columns) {
        if (rows <= 0) {
            throw new IllegalArgumentException("Количество строк матрицы должно было быть натуральным числом, а было " + rows);
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("Количество столбцов матрицы должно было быть натуральным числом, а было " + columns);
        }

        rowLength = rows;
        columnLength = columns;
        array = new double[rows * columns];
    }

    /**
     * Создает новую пустую матрицу-вектор с одной строкой и заданным количеством столбцов
     *
     * @param columns Количество столбцов матрицы
     */
    public Matrix(int columns) {
        this(1, columns);
    }

    public Matrix(int columns, double... matrix) {
        this(1, columns, matrix);
    }

    public Matrix(double... matrix) {
        this(1, matrix.length, matrix);
    }

    /**
     * Создает новую матрицу с заданным количеством строк и столбцов с данными из массива
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @param matrix  Данные матрицы
     * @throws IllegalArgumentException если количество ячеек матрицы не совпадает с количеством элементов массива
     */
    public Matrix(int rows, int columns, double... matrix) {
        this(rows, columns);
        if (rows * columns != matrix.length)
            throw new IllegalArgumentException(
                    "Количество ячеек матрицы (%d*%d=%d) не совпадает с количеством элементов массива (%d)"
                            .formatted(rows, columns, rows * columns, matrix.length));
        array = matrix;
    }

    /**
     * Создает новую матрицу с заданным количеством строк и столбцов, заполняя ее данными инициализатора
     *
     * @param rows        Количество строк матрицы
     * @param columns     Количество столбцов матрицы
     * @param initializer Функция, поставляющая данные для матрицы
     */
    public Matrix(int rows, int columns, DoubleSupplier initializer) {
        this(rows, columns);

        for (int i = 0; i < array.length; i++) {
            array[i] = initializer.getAsDouble();
        }
    }

    /**
     * Находит элемент матрицы по заданным координатам
     *
     * @param row    Строка, в которой искать элемент
     * @param column Столбец, в которой искать элемент
     * @return Элемент матрицы, находящийся в заданных строке и столбце
     * @throws IllegalArgumentException если значение строки и/или столбца выходит за допустимые границы
     */
    public double getItem(int row, int column) {
        if (row < 0 || row >= rowLength) {
            throw new IllegalArgumentException(
                    "Значение строки должно было быть числом в диапазоне [0, %d], а было %d".formatted(rowLength - 1, row));
        }
        if (column < 0 || column >= columnLength) {
            throw new IllegalArgumentException(
                    "Значение столбца должно было быть числом в диапазоне [0, %d], а было %d".formatted(columnLength - 1, column));
        }
        return array[columnLength * row + column];
    }

    /**
     * Приводит матрицу к одномерному массиву. Массив составляется из матрицы построчно
     *
     * @return Массив данных матрицы в формате одномерного массива
     */
    public double[] getArray() {
        return array;
    }

    /**
     * Производит поэлементное сложение матриц.
     *
     * @param m Матрица-слагаемое
     * @return Сумма матриц
     * @throws ArithmeticException если у матриц не совпадает количество строк
     * @throws ArithmeticException если у матриц не совпадает количество столбцов
     */
    public Matrix add(Matrix m) {
        rowsMismatch(this, m);
        columnsMismatch(this, m);

        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i] + m.array[i];

        return new Matrix(rowLength, columnLength, result);
    }

    /**
     * Производит поэлементное вычитание матриц.
     *
     * @param m Матрица-вычитаемое
     * @return Разница матриц
     * @throws ArithmeticException если у матриц не совпадает количество строк
     * @throws ArithmeticException если у матриц не совпадает количество столбцов
     */
    public Matrix sub(Matrix m) {
        rowsMismatch(this, m);
        columnsMismatch(this, m);

        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i] - m.array[i];

        return new Matrix(rowLength, columnLength, result);
    }

    /**
     * Производит поэлементное умножение матриц.
     *
     * @param m Матрица-множитель
     * @return Поэлементное произведение матриц
     * @throws ArithmeticException если у матриц не совпадает количество строк
     * @throws ArithmeticException если у матриц не совпадает количество столбцов
     */
    public Matrix prod(Matrix m) {
        rowsMismatch(this, m);
        columnsMismatch(this, m);

        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i] * m.array[i];

        return new Matrix(rowLength, columnLength, result);
    }

    /**
     * Производит матричное умножение матриц.
     *
     * @param m Матрица-множитель
     * @return Матричное произведение матриц
     * @throws ArithmeticException если количество столбцов левой матрицы не совпадает с количеством столбцов правой матрицы.
     */
    public Matrix mul(Matrix m) {
        rowsColumnsMismatch(this, m);

        double[] result = new double[rowLength * m.columnLength];
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < m.columnLength; j++) {
                for (int k = 0; k < columnLength; k++) {
                    result[i * m.columnLength + j] += array[i * columnLength + k] * m.array[k * m.columnLength + j];
                }
            }
        }

        return new Matrix(rowLength, m.columnLength, result);
    }

    public Matrix tMul(Matrix m) {
        rowsMismatch(this, m);

        double[] result = new double[columnLength * m.columnLength];

        for (int i = 0; i < columnLength; i++)
            for (int j = 0; j < m.columnLength; j++)
                for (int k = 0; k < rowLength; k++) {
                    double a = array[k * columnLength + i];
                    double b = m.array[k * m.columnLength + j];
                    result[i * m.columnLength + j] += a * b;
                }

        return new Matrix(columnLength, m.columnLength, result);
    }

    public Matrix mulT(Matrix m) {
        columnsMismatch(this, m);

        double[] result = new double[rowLength * m.rowLength];

        for (int i = 0; i < rowLength; i++)
            for (int j = 0; j < m.rowLength; j++)
                for (int k = 0; k < columnLength; k++) {
                    double a = array[i * columnLength + k];
                    double b = m.array[j * m.columnLength + k];
                    result[i * m.rowLength + j] += a * b;
                }

        return new Matrix(rowLength, m.rowLength, result);
    }

    /**
     * Транспонирует матрицу
     *
     * @return Матрица, у которой количество строк и столбцов равно
     * количеству столбцов и строк исходной матрицы
     */
    public Matrix T() {
        double[] result = new double[array.length];
        for (int i = 0; i < rowLength; i++)
            for (int j = 0; j < columnLength; j++)
                result[j * rowLength + i] = array[i * columnLength + j];

        return new Matrix(columnLength, rowLength, result);
    }
    /**
     * Масштабирует матрицу
     *
     * @param n Коэффициент масштабирования
     * @return Матрица, каждый элемент которой умножен на коэффициент масштабирования
     */
    public Matrix scale(double n) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i] * n;

        return new Matrix(rowLength, columnLength, result);
    }

    /**
     * Отображает матрицу в матрицу такой же размерности по правилу функции отображения.
     *
     * @param function Функция отображения
     * @return Матрица, к каждому элементу которой применена функция отображения
     */
    public Matrix apply(DoubleFunction<Double> function) {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = function.apply(array[i]);

        return new Matrix(rowLength, columnLength, result);
    }

    /**
     * Нормирует матрицу
     *
     * @return Матрица у которой все элементы находятся в диапазоне [0, 1)
     */
    public Matrix normalize() {
        return scale(1 / norm());
    }

    /**
     * Вычисляет норму второго порядка матрицы
     *
     * @return Норма второго порядка матрицы
     */
    public double norm() {
        return Math.sqrt(sqrsSum());
    }

    /**
     * Вычисляет сумму элементов матрицы
     *
     * @return Сумма элементов матрицы
     */
    public double sum() {
        double sum = 0.0;
        for (double item : array) sum += item;
        return sum;
    }

    /**
     * Вычисляет сумму квадратов элементов матрицы
     * @return Сумма квадратов элементов матрицы
     */
    public double sqrsSum() {
        double sum = 0.0;
        for (double item : array) sum += item * item;
        return sum;
    }

    /**
     * Вычисляет среднее арифметическое элементов матрицы
     *
     * @return Среднее арифметическое элементов матрицы
     */
    public double average() {
        return sum() / array.length;
    }

    /**
     * Обнуляет данные матриц, сохраняя структуру
     *
     * @param matrices Обнуляемые матрицы
     */
    public static void zeros(Matrix... matrices) {
        for (Matrix m : matrices) Arrays.fill(m.array, 0.0);
    }

    /**
     * Печатает матрицы в стандартный поток вывода
     *
     * @param matrices печатаемые матрицы
     */
    public static void print(Matrix... matrices) {
        String[][] strings = new String[matrices.length][];
        int maxRow = 0;
        for (int i = 0; i < matrices.length; i++) {
            strings[i] = matrices[i].toStringsByRows();
            if (strings[i].length > maxRow) {
                maxRow = strings[i].length;
            }
        }

        for (int row = 0; row < maxRow; row++) {
            for (String[] string : strings) {
                if (string.length > row) {
                    System.out.print(string[row] + "  ");
                } else {
                    System.out.print(" ".repeat(string[0].length()) + "  ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Представляет матрицу как массив строк по строкам матрицы
     *
     * @return Построчное представление матрицы
     */
    public String[] toStringsByRows() {
        String[] rows = new String[rowLength];
        StringBuilder s;
        for (int i = 0; i < rowLength; i++) {
            s = new StringBuilder("[");
            for (int j = 0; j < columnLength; j++) {
                s.append("%+.6f, ".formatted(getItem(i, j)));
            }
            s.delete(s.length() - 2, s.length());
            s.append("]");
            rows[i] = s.toString();
        }

        return rows;
    }

    /**
     * Создает копию матрицы
     *
     * @return Независимая копия матрицы
     */
    @SneakyThrows
    @Override
    public Matrix clone() {
        Matrix clone = (Matrix) super.clone();
        clone.rowLength = rowLength;
        clone.columnLength = columnLength;
        clone.array = Arrays.copyOf(array, array.length);
        return clone;
    }

    @Override
    public String toString() {
        return "Matrix{%dx%d, data=%s}".formatted(rowLength, columnLength, Arrays.toString(array));
    }

    public String summary() {
        return "Matrix{%dx%d}".formatted(rowLength, columnLength);
    }

    @Override
    public Iterator<Double> iterator() {
        return Arrays.stream(array).iterator();
    }

    private void columnsMismatch(Matrix left, Matrix right) {
        if (left.columnLength != right.columnLength)
            throw new ArithmeticException("Не совпадает количество столбцов: %s и %s"
                    .formatted(left.columnLength, right.columnLength));
    }

    private void rowsMismatch(Matrix left, Matrix right) {
        if (left.rowLength != right.rowLength)

            throw new ArithmeticException("Не совпадает количество строк: %s и %s"
                    .formatted(left.rowLength, right.rowLength));
    }

    private void rowsColumnsMismatch(Matrix left, Matrix right) {
        if (left.columnLength != right.rowLength)
            throw new ArithmeticException("Не совпадает количество столбцов левой матрицы и строк правой матрицы: %s и %s"
                    .formatted(left.columnLength, right.rowLength));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(rowLength);
        out.writeInt(columnLength);
        for (double v : array) {
            out.writeDouble(v);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        rowLength = in.readInt();
        columnLength = in.readInt();
        array = new double[rowLength * columnLength];
        for (int i = 0; i < array.length; i++) {
            array[i] = in.readDouble();
        }
    }
}
