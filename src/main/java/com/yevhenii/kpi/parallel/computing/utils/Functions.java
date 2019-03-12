package com.yevhenii.kpi.parallel.computing.utils;

import java.util.*;
import java.util.stream.Collectors;

public class Functions {
    private static final Comparator<Double> comparator = Comparator.comparingDouble(Math::abs);

    public static double kahanSummation(double[] arr) {
        List<Double> sorted = Arrays.stream(arr)
                .boxed()
                .sorted(comparator.reversed())
                .collect(Collectors.toList());

        double sum = 0.0;
        double c = 0.0;
        for (int i = 0; i < arr.length; i++) {
            double y = sorted.get(i) - c;
            double t = sum + y;
            c = (t - sum) - y;
            sum = t;
        }

        return sum;
    }
}
