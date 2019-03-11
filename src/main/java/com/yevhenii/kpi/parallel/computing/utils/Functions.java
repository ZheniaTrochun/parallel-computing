package com.yevhenii.kpi.parallel.computing.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class Functions {
    public static List<Double> multiply(List<Double> vector, List<List<Double>> matrix) {
        List<Double> resultVector = new ArrayList<>(vector.size());

        for (int i = 0; i < vector.size(); i++) {
            double res = 0;
            for (int j = 0; j < vector.size(); j++) {
                res += vector.get(j) * matrix.get(j).get(i);
            }
            resultVector.add(res);
        }

        return resultVector;
    }

    public static List<Double> multiplyByNum(List<Double> vector, Double number) {
        List<Double> resultVector = new ArrayList<>(vector.size());

        for (int i = 0; i < vector.size(); i++) {
            resultVector.add(vector.get(i) * number);
        }

        return resultVector;
    }

    public static List<List<Double>> multiplyMatrices(List<List<Double>> first, List<List<Double>> second) {
        List<List<Double>> result = new ArrayList<>(first.size());

        for (int i = 0; i < first.size(); i++) {
            List<Double> curr = new ArrayList<>(first.size());
            for (int j = 0; j < first.size(); j++) {
                double res = 0;
                for (int k = 0; k < first.size(); k++) {
                    res += first.get(i).get(k) * second.get(j).get(k);
                }
                curr.add(res);
            }
            result.add(curr);
        }

        return result;
    }

    public static List<List<Double>> sumMatrices(List<List<Double>> first, List<List<Double>> second) {
        List<List<Double>> result = new ArrayList<>(first.size());

        for (int i = 0; i < first.size(); i++) {
            List<Double> curr = new ArrayList<>(first.size());
            for (int j = 0; j < first.size(); j++) {
                curr.add(first.get(i).get(j) + second.get(i).get(j));
            }
            result.add(curr);
        }

        return result;
    }

    public static List<List<Double>> multiplyMatrixByNum(List<List<Double>> matrix, Double num) {
        List<List<Double>> result = new ArrayList<>(matrix.size());

        for (int i = 0; i < matrix.size(); i++) {
            List<Double> curr = new ArrayList<>(matrix.size());
            for (int j = 0; j < matrix.size(); j++) {
                curr.add(matrix.get(i).get(j) * num);
            }
            result.add(curr);
        }

        return result;
    }

    public static List<List<Double>> substructMatrices(List<List<Double>> first, List<List<Double>> second) {
        List<List<Double>> result = new ArrayList<>(first.size());

        for (int i = 0; i < first.size(); i++) {
            List<Double> curr = new ArrayList<>(first.size());
            for (int j = 0; j < first.size(); j++) {
                curr.add(first.get(i).get(j) - second.get(i).get(j));
            }
            result.add(curr);
        }

        return result;
    }

    public static List<Double> sumVectors(List<Double> first, List<Double> second) {
        return combineVectors(first, second, (a, b) -> a + b);
    }
//    public static List<Double> sumVectors(List<Double> first, List<Double> second) {
//        List<Double> result = new ArrayList<>(first.size());
//
//        for (int i = 0; i < first.size(); i++) {
//            result.set(i, first.get(i) + second.get(i));
//        }
//
//        return result;
//    }

    public static List<Double> substructVectors(List<Double> first, List<Double> second) {
        return combineVectors(first, second, (a, b) -> a - b);
    }
//    public static List<Double> substructVectors(List<Double> first, List<Double> second) {
//        List<Double> result = new ArrayList<>(first.size());
//
//        for (int i = 0; i < first.size(); i++) {
//            result.set(i, first.get(i) - second.get(i));
//        }
//
//        return result;
//    }

    public static double min(List<Double> vector) {
        return vector.stream()
                .min(Comparator.naturalOrder())
                .get();
    }

    private static List<Double> combineVectors(List<Double> first, List<Double> second, BiFunction<Double, Double, Double> combinator) {
        List<Double> result = new ArrayList<>(first.size());

        for (int i = 0; i < first.size(); i++) {
            result.add(combinator.apply(first.get(i), second.get(i)));
        }

        return result;
    }
}
