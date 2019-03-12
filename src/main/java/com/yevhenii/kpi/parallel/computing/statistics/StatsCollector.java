package com.yevhenii.kpi.parallel.computing.statistics;

import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class StatsCollector {

    public static void main(String[] args) {
        List<StatsHolder> results = measureAll();
        JsonUtils.writeStatistics(results)
                .ifPresent(System.out::println);
    }

    public static StatsHolder measurePerformance(String tag, Runnable task) {
        Map<String, Double> res = Runner.of(task).calculateAverageTime();
        return new StatsHolder(tag, Runner.getRunsNumber(), res);
    }

    public static List<StatsHolder> measureAll() {
        Random rand = new Random();
        Data data = new Data(new Generator(rand));

        StatsHolder threads = measurePerformance(
                "threads",
                from(() -> new com.yevhenii.kpi.parallel.computing.lab1.threads.CalculationThreads(data).start().joinAll())
        );

        StatsHolder countdown = measurePerformance(
                "countdown",
                from(() -> new com.yevhenii.kpi.parallel.computing.lab2.countdown.CalculationThreads(data).start().joinAll())
        );

        StatsHolder executor = measurePerformance(
                "executor",
                from(() -> new com.yevhenii.kpi.parallel.computing.lab3.executor.CalculationThreads(data).start().get())
        );

        StatsHolder future = measurePerformance(
                "future",
                from(() -> new com.yevhenii.kpi.parallel.computing.lab4.future.Calculations(data).start().get())
        );

        StatsHolder blockingQueue = measurePerformance(
                "blocking-queue",
                from(() -> new com.yevhenii.kpi.parallel.computing.lab5.blocking.queue.Calculations(data).start().get())
        );

        StatsHolder tasks = measurePerformance(
                "tasks",
                from(() -> new com.yevhenii.kpi.parallel.computing.lab6.tasks.Calculations(data).start())
        );

        return Arrays.asList(threads, countdown, executor, future, blockingQueue, tasks);
    }

    private static Runnable from(UnsafeRunnable unsafe) {
        return () -> {
            try {
                unsafe.run();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                throw new Error(e);
            }
        };
    }

    @FunctionalInterface
    private interface UnsafeRunnable {
        void run() throws InterruptedException, ExecutionException;
    }
}
