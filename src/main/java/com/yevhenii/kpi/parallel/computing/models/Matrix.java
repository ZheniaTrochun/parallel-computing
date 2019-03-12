package com.yevhenii.kpi.parallel.computing.models;

import com.yevhenii.kpi.parallel.computing.utils.Functions;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {
    private final double[][] state;
    private final int size;

    public Matrix(double[][] state) {
        if (state.length == 0 || state.length != state[0].length) {
            throw new ArithmeticException();
        }
        this.state = state;
        this.size = state.length;
    }

    public Matrix multiply(Matrix other) {
        if (size != other.size) {
            throw new ArithmeticException();
        }

        double[][] resultState = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double[] multiplies = new double[size];
                for (int k = 0; k < size; k++) {
                    multiplies[k] = state[i][k] * other.state[j][k];
                }
                resultState[i][j] = Functions.kahanSummation(multiplies);
            }
        }

        return new Matrix(resultState);
    }

    public Matrix add(Matrix other) {
        if (size != other.size) {
            throw new ArithmeticException();
        }

        double[][] resultState = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                resultState[i][j] = state[i][j] + other.state[i][j];
            }
        }

        return new Matrix(resultState);
    }

    public Matrix substruct(Matrix other) {
        if (size != other.size) {
            throw new ArithmeticException();
        }

        double[][] resultState = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                resultState[i][j] = state[i][j] - other.state[i][j];
            }
        }

        return new Matrix(resultState);
    }

    public Matrix multiply(double number) {
        double[][] resultState = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                resultState[i][j] = state[i][j] * number;
            }
        }

        return new Matrix(resultState);
    }

    public int getSize() {
        return size;
    }

    double[][] getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matrix)) return false;
        Matrix matrix = (Matrix) o;

        if (getSize() != matrix.getSize()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (state[i][j] != matrix.state[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(getSize());
        result = 31 * result + Arrays.hashCode(getState());
        return result;
    }
}
