package com.yevhenii.kpi.parallel.computing.statistics;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Runner {
    private static final String PROFILING_SEPARATOR = "finished in";
    private static final int WARMUP_RUNS = 2;
    private static final int RUNS = 10;

    private final Runnable task;

    private Runner(Runnable task) {
        this.task = task;
    }

    public static Runner of(Runnable task) {
        return new Runner(task);
    }

    public Map<String, Double> calculateAverageTime() {
        warmUp();

        Map<String, List<Map.Entry<String, Long>>> results =
                IntStream.range(0, RUNS)
                        .boxed()
                        .flatMap(i -> getSingleRunPerformance().entrySet().stream())
                        .collect(Collectors.groupingBy(Map.Entry::getKey));

        return results.entrySet()
                .stream()
                .map(entry -> Pair.of(entry.getKey(), average(entry.getValue())))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public Map<String, Long> getSingleRunPerformance() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        task.run();

        String sout = out.toString();

        if (sout.contains("xception")) {
            throw new Error(sout);
        }

        return Arrays.stream(sout.split("\n"))
                .filter(line -> line.contains(PROFILING_SEPARATOR))
                .map(this::parseLine)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public static int getRunsNumber() {
        return RUNS;
    }

    private void warmUp() {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            task.run();
        }
    }

    private Pair<String, Long> parseLine(String line) {
        String[] splitted = line.split(PROFILING_SEPARATOR);
        return Pair.of(
                splitted[0].trim(),
                Long.valueOf(splitted[1].replace("ms", "").trim())
        );
    }

    private Double average(List<Map.Entry<String, Long>> entries) {
        return entries.stream()
                .mapToLong(Map.Entry::getValue)
                .average()
                .getAsDouble();
    }

    private static class Pair<L, R> {
        private final L left;
        private final R right;

        private Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public static <L, R> Pair<L, R> of(L left, R right) {
            return new Pair<>(left, right);
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}
