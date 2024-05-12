package com.synapse.core.matrix;

import java.io.Externalizable;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

/**
 * Класс двумерного массива (матрицы) для алгебраических вычислений
 */
public interface Matrix extends Cloneable, Iterable<Double>, Externalizable {

    /**
     * Создает новую пустую матрицу с заданным количеством строк и столбцов
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @throws IllegalArgumentException если количество строк и/или столбцов не натуральное число
     */
    static Matrix create(int rows, int columns) {
        return create(rows, columns, new double[rows * columns]);
    }

    /**
     * Создает новую пустую матрицу-вектор с одной строкой и заданным количеством столбцов
     *
     * @param columns Количество столбцов матрицы
     */
    static Matrix create(int columns) {
        return create(1, columns);
    }

    /**
     * Создает новую пустую матрицу-вектор с одной строкой и заданными данными
     *
     * @param data данные матрицы
     */
    static Matrix create(double... data) {
        return create(1, data.length, data);
    }

    /**
     * Создает новую матрицу с заданным количеством строк и столбцов с данными из массива
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @param data    Данные матрицы
     * @throws IllegalArgumentException если количество ячеек матрицы не совпадает с количеством элементов массива
     */
    static Matrix create(int rows, int columns, double... data) {
        return MatrixSettings.getMatrixClass().createInstance(rows, columns, data);
    }

    /**
     * Создает новую матрицу с заданным количеством строк и столбцов, заполняя ее данными инициализатора
     *
     * @param rows        Количество строк матрицы
     * @param columns     Количество столбцов матрицы
     * @param initializer Функция, поставляющая данные для матрицы
     */
    static Matrix create(int rows, int columns, DoubleSupplier initializer) {
        double[] data = new double[rows * columns];
        for (int i = 0; i < data.length; i++) {
            data[i] = initializer.getAsDouble();
        }
        return MatrixSettings.getMatrixClass().createInstance(rows, columns, data);
    }

    /**
     * Преобразует матрицу к классу-реализации Matrix, которая задана в MatrixSettings
     * @param matrix Матрица для преобразования
     * @return Преобразованная матрица с теми же свойствами и данными
     */
    static Matrix translate(Matrix matrix) {
        return create(matrix.getRowsNumber(), matrix.getColumnsNumber(), matrix.getArray());
    }

    /**
     * Создает новую матрицу с заданным количеством строк и столбцов с данными из массива
     *
     * @param rows    Количество строк матрицы
     * @param columns Количество столбцов матрицы
     * @param data  Данные матрицы
     * @throws IllegalArgumentException если количество ячеек матрицы не совпадает с количеством элементов массива
     */
    Matrix createInstance(int rows, int columns, double... data);

    /**
     * Возвращает количество строк матрицы
     * @return Значение количества строк матрицы
     */
    int getRowsNumber();

    /**
     * Возвращает количество столбцов матрицы
     * @return Значение количества столбцов матрицы
     */
    int getColumnsNumber();

    /**
     * Находит элемент матрицы по заданным координатам
     *
     * @param row    Строка, в которой искать элемент
     * @param column Столбец, в которой искать элемент
     * @return Элемент матрицы, находящийся в заданных строке и столбце
     * @throws IllegalArgumentException если значение строки и/или столбца выходит за допустимые границы
     */
    double getItem(int row, int column);

    /**
     * Приводит матрицу к одномерному массиву. Массив составляется из матрицы построчно
     *
     * @return Массив данных матрицы в формате одномерного массива
     */
    double[] getArray();

    /**
     * Производит поэлементное сложение матриц.
     *
     * @param matrix Матрица-слагаемое
     * @return Сумма матриц
     * @throws ArithmeticException если у матриц не совпадает количество строк
     * @throws ArithmeticException если у матриц не совпадает количество столбцов
     */
    Matrix add(Matrix matrix);

    /**
     * Производит поэлементное вычитание матриц.
     *
     * @param matrix Матрица-вычитаемое
     * @return Разница матриц
     * @throws ArithmeticException если у матриц не совпадает количество строк
     * @throws ArithmeticException если у матриц не совпадает количество столбцов
     */
    Matrix sub(Matrix matrix);

    /**
     * Производит поэлементное умножение матриц.
     *
     * @param matrix Матрица-множитель
     * @return Поэлементное произведение матриц
     * @throws ArithmeticException если у матриц не совпадает количество строк
     * @throws ArithmeticException если у матриц не совпадает количество столбцов
     */
    Matrix prod(Matrix matrix);

    /**
     * Производит матричное умножение матриц.
     *
     * @param matrix Матрица-множитель
     * @return Матричное произведение матриц
     * @throws ArithmeticException если количество столбцов левой матрицы не совпадает с количеством столбцов правой матрицы.
     */
    Matrix mul(Matrix matrix);

    /**
     * Производит матричное умножение матриц с предварительным транспонированием первой матрицы-множителя.
     *
     * @param matrix Матрица-множитель
     * @return Матричное произведение матриц
     * @throws ArithmeticException если количество столбцов левой матрицы не совпадает с количеством столбцов правой матрицы.
     */
    Matrix tMul(Matrix matrix);

    /**
     * Производит матричное умножение матриц с предварительным транспонированием второй матрицы-множителя.
     *
     * @param matrix Матрица-множитель
     * @return Матричное произведение матриц
     * @throws ArithmeticException если количество столбцов левой матрицы не совпадает с количеством столбцов правой матрицы.
     */
    Matrix mulT(Matrix matrix);

    /**
     * Транспонирует матрицу
     *
     * @return Матрица, у которой количество строк и столбцов равно
     * количеству столбцов и строк исходной матрицы
     */
    Matrix Trans();

    /**
     * Масштабирует матрицу
     *
     * @param n Коэффициент масштабирования
     * @return Матрица, каждый элемент которой умножен на коэффициент масштабирования
     */
    Matrix scale(double n);

    /**
     * Производит поэлементное сложение матриц с предварительным масштабированием второй матрицы-множителя
     * @param scale Коэффициент масштабирования
     * @param matrix Матрица-множитель
     * @return Результат умножения с масштабированием
     */
    Matrix scaleAdd(double scale, Matrix matrix);

    /**
     * Отображает матрицу в матрицу такой же размерности по правилу функции отображения.
     *
     * @param function Функция отображения
     * @return Матрица, к каждому элементу которой применена функция отображения
     */
    Matrix apply(DoubleFunction<Double> function);

    /**
     * Нормирует матрицу
     *
     * @return Матрица у которой все элементы находятся в диапазоне [0, 1)
     */
    Matrix normalize();

    /**
     * Вычисляет норму второго порядка матрицы
     *
     * @return Норма второго порядка матрицы
     */
    double norm();

    /**
     * Вычисляет сумму элементов матрицы
     *
     * @return Сумма элементов матрицы
     */
    double sum();

    /**
     * Вычисляет сумму квадратов элементов матрицы
     *
     * @return Сумма квадратов элементов матрицы
     */
    double sqrsSum();

    /**
     * Вычисляет среднее арифметическое элементов матрицы
     *
     * @return Среднее арифметическое элементов матрицы
     */
    double average();

    /**
     * Обнуляет данные матрицы, сохраняя структуру
     */
    void zeros();

    static void zeros(Matrix... matrices) {
        for (Matrix matrix : matrices) {
            matrix.zeros();
        }
    }
    Matrix clone();


//
//    /**
//     * Печатает матрицы в стандартный поток вывода
//     *
//     * @param matrices печатаемые матрицы
//     */
//    static void print(Matrix... matrices) {
//        String[][] strings = new String[matrices.length][];
//        int maxRow = 0;
//        for (int i = 0; i < matrices.length; i++) {
//            strings[i] = matrices[i].getReport().toArray(new String[0]);
//            if (strings[i].length > maxRow) {
//                maxRow = strings[i].length;
//            }
//        }
//
//        for (int row = 0; row < maxRow; row++) {
//            for (String[] string : strings) {
//                if (string.length > row) {
//                    System.out.print(string[row] + "  ");
//                } else {
//                    System.out.print(" ".repeat(string[0].length()) + "  ");
//                }
//            }
//            System.out.println();
//        }
//    }
}
