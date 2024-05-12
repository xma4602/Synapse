package com.synapse.core.matrix;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

/**
 * Класс двумерного массива (матрицы) для алгебраических вычислений
 */
@EqualsAndHashCode
@NoArgsConstructor
public class MatrixJava implements Matrix {

    @Serial
    private static final long serialVersionUID = 5679755609199712210L;
    /**
     * Массив данных матрицы
     */
    private double[] array;

    /**
     * Количество строк матрицы
     */
    private int rowLength;

    /**
     * Количество столбцов матрицы
     */
    private int columnLength;

    /**
     * Создает новую пустую матрицу с заданным количеством строк и столбцов
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @throws IllegalArgumentException если количество строк и/или столбцов не натуральное число
     */
    public MatrixJava(int rows, int columns) {
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
     * Создает новую матрицу с заданным количеством строк и столбцов с данными из массива
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @param matrix  Данные матрицы
     * @throws IllegalArgumentException если количество ячеек матрицы не совпадает с количеством элементов массива
     */
    public MatrixJava(int rows, int columns, double... matrix) {
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
    public MatrixJava(int rows, int columns, DoubleSupplier initializer) {
        this(rows, columns);

        for (int i = 0; i < array.length; i++) {
            array[i] = initializer.getAsDouble();
        }
    }

    @Override
    public Matrix createInstance(int rows, int columns, double... matrix) {
        return new MatrixJava(rows, columns, matrix);
    }

    @Override
    public int getRowLength() {
        return rowLength;
    }

    @Override
    public int getColumnLength() {
        return columnLength;
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
            result[i] = array[i] + m.getArray()[i];

        return new MatrixJava(rowLength, columnLength, result);
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
            result[i] = array[i] - m.getArray()[i];

        return new MatrixJava(rowLength, columnLength, result);
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
            result[i] = array[i] * m.getArray()[i];

        return new MatrixJava(rowLength, columnLength, result);
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

        double[] result = new double[rowLength * m.getColumnLength()];
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < m.getColumnLength(); j++) {
                for (int k = 0; k < columnLength; k++) {
                    result[i * m.getColumnLength() + j] += array[i * columnLength + k] * m.getArray()[k * m.getColumnLength() + j];
                }
            }
        }

        return new MatrixJava(rowLength, m.getColumnLength(), result);
    }

    public Matrix tMul(Matrix m) {
        rowsMismatch(this, m);

        double[] result = new double[columnLength * m.getColumnLength()];

        for (int i = 0; i < columnLength; i++)
            for (int j = 0; j < m.getColumnLength(); j++)
                for (int k = 0; k < rowLength; k++) {
                    double a = array[k * columnLength + i];
                    double b = m.getArray()[k * m.getColumnLength() + j];
                    result[i * m.getColumnLength() + j] += a * b;
                }

        return new MatrixJava(columnLength, m.getColumnLength(), result);
    }

    public Matrix mulT(Matrix m) {
        columnsMismatch(this, m);

        double[] result = new double[rowLength * m.getRowLength()];

        for (int i = 0; i < rowLength; i++)
            for (int j = 0; j < m.getRowLength(); j++)
                for (int k = 0; k < columnLength; k++) {
                    double a = array[i * columnLength + k];
                    double b = m.getArray()[j * m.getColumnLength() + k];
                    result[i * m.getRowLength() + j] += a * b;
                }

        return new MatrixJava(rowLength, m.getRowLength(), result);
    }

    /**
     * Транспонирует матрицу
     *
     * @return Матрица, у которой количество строк и столбцов равно
     * количеству столбцов и строк исходной матрицы
     */
    public Matrix Trans() {
        double[] result = new double[array.length];
        for (int i = 0; i < rowLength; i++)
            for (int j = 0; j < columnLength; j++)
                result[j * rowLength + i] = array[i * columnLength + j];

        return new MatrixJava(columnLength, rowLength, result);
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

        return new MatrixJava(rowLength, columnLength, result);
    }

    @Override
    public Matrix scaleAdd(double scale, Matrix matrix) {
        rowsMismatch(this, matrix);
        columnsMismatch(this, matrix);

        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i] + scale * matrix.getArray()[i];

        return new MatrixJava(rowLength, columnLength, result);
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

        return new MatrixJava(rowLength, columnLength, result);
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
     *
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

    @Override
    public void zeros() {
        Arrays.fill(array, 0.0);
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
    @Override
    public MatrixJava clone() {
        return new MatrixJava(rowLength, columnLength, Arrays.copyOf(array, array.length));
    }

    @Override
    public String toString() {
        return "Matrix{%dx%d}".formatted(rowLength, columnLength);
    }

    @Override
    public Iterator<Double> iterator() {
        return Arrays.stream(array).iterator();
    }

    private void columnsMismatch(Matrix left, Matrix right) {
        if (left.getColumnLength() != right.getColumnLength())
            throw new ArithmeticException("Не совпадает количество столбцов: %s и %s"
                    .formatted(left.getColumnLength(), right.getColumnLength()));
    }

    private void rowsMismatch(Matrix left, Matrix right) {
        if (left.getRowLength() != right.getRowLength())

            throw new ArithmeticException("Не совпадает количество строк: %s и %s"
                    .formatted(left.getRowLength(), right.getRowLength()));
    }

    private void rowsColumnsMismatch(Matrix left, Matrix right) {
        if (left.getColumnLength() != right.getRowLength())
            throw new ArithmeticException("Не совпадает количество столбцов левой матрицы и строк правой матрицы: %s и %s"
                    .formatted(left.getColumnLength(), right.getRowLength()));
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
