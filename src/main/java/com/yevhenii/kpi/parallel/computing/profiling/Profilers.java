package com.yevhenii.kpi.parallel.computing.profiling;

import java.util.function.Supplier;

public class Profilers {
    private static final String TIME_LOG_TEMPLATE = "%s finished in %d ms";
    private static final String START_LOG_TEMPLATE = "%s started";

    public static <T> Supplier<T> profile(String message, Supplier<T> supplier) {
        return () -> {
            System.out.println(String.format(START_LOG_TEMPLATE, message));
            long start = System.currentTimeMillis();
            T res = supplier.get();
            long end = System.currentTimeMillis();
            System.out.println(String.format(TIME_LOG_TEMPLATE, message, end - start));

            return res;
        };
    }
}
