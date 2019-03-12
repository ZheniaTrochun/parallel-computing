package com.yevhenii.kpi.parallel.computing.utils;

import com.yevhenii.kpi.parallel.computing.models.Matrix;
import com.yevhenii.kpi.parallel.computing.models.Vector;

import java.util.Random;
import java.util.stream.DoubleStream;

public class Generator {
    private final Random rand;

    public Generator(Random rand) {
        this.rand = rand;
    }

    public double getDouble() {
        return rand.nextBoolean() ?
                rand.nextDouble() :
                rand.nextDouble() * (rand.nextInt(100000) - 100000);
    }

    public double[] getRawVector(int size) {
        double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = getDouble();
        }

        return vector;
    }

    public Vector getVector(int size) {
        return new Vector(
                DoubleStream.iterate(getDouble(), i -> getDouble())
                        .limit(size)
                        .toArray()
        );
    }

    public double[][] getRawMatrix(int size) {
        double[][] matrix = new double[size][];

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = new double[size];
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = getDouble();
            }
        }

        return matrix;
    }

    public Matrix getMatrix(int size) {
        return new Matrix(getRawMatrix(size));
    }
}
