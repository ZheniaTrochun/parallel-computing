package com.yevhenii.kpi.parallel.computing.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@FunctionalInterface
public interface Generator {

    double getDouble();

    default double[] getRawVector(int size) {
        double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = getDouble();
        }

        return vector;
    }

    default List<Double> getVector(int size) {
        return DoubleStream.iterate(getDouble(), i -> getDouble())
                .limit(size)
                .boxed()
                .collect(Collectors.toList());
    }

    default double[][] getRawMatrix(int size) {
        double[][] matrix = new double[size][];

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = new double[size];
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = getDouble();
            }
        }

        return matrix;
    }

    default List<List<Double>> getMatrix(int size) {
        return DoubleStream.iterate(getDouble(), i -> getDouble())
                .boxed()
                .limit(size)
                .map(row -> getVector(size))
                .collect(Collectors.toList());
    }
}
