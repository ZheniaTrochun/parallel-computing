package com.yevhenii.kpi.parallel.computing.models;

import com.yevhenii.kpi.parallel.computing.utils.Functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class Vector {
    private final double[] state;
    private final int size;

    public Vector(double[] state) {
        this.state = state;
        this.size = state.length;
    }

    public Vector add(Vector other) {
        if (size != other.size) {
            throw new ArithmeticException();
        }

        double[] resultState = new double[size];
        for (int i = 0; i < size; i++) {
            resultState[i] = state[i] + other.state[i];
        }

        return new Vector(resultState);
    }

    public Vector minus() {
        double[] resultState = new double[size];
        for (int i = 0; i < size; i++) {
            resultState[i] = -state[i];
        }

        return new Vector(resultState);
    }

    public Vector substruct(Vector other) {
        return add(other.minus());
    }

    public Vector multiply(double number) {
        double[] resultState = new double[size];
        for (int i = 0; i < size; i++) {
            resultState[i] = state[i] * number;
        }

        return new Vector(resultState);
    }

    public Vector multiply(Matrix matrix) {
        if (size != matrix.getSize()) {
            throw new ArithmeticException();
        }

        double[] resultState = new double[size];
        double[][] matrixState = matrix.getState();

        for (int i = 0; i < size; i++) {
            double[] multiplies = new double[size];
            for (int j = 0; j < size; j++) {
                multiplies[j] = state[j] * matrixState[j][i];
            }
            resultState[i] = Functions.kahanSummation(multiplies);
        }

        return new Vector(resultState);
    }

    public double min() {
        return Arrays.stream(state).min().getAsDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;
        Vector vector = (Vector) o;
        return size == vector.size &&
                Arrays.equals(state, vector.state);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(state);
        return result;
    }
}
