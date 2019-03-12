package com.yevhenii.kpi.parallel.computing.lab1.threads;

import com.yevhenii.kpi.parallel.computing.models.ResultData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ThreadHolder {

    private final List<Thread> threads;

    private ResultData resultData;

    public ThreadHolder(List<Thread> threads, ResultData resultData) {
        this.threads = threads;
        this.resultData = resultData;
    }

    public ThreadHolder(ResultData resultData) {
        this.threads = new ArrayList<>();
        this.resultData = resultData;
    }

    public ThreadHolder start(Runnable calculations) {
        Thread thread = new Thread(calculations);
        thread.start();

        List<Thread> updatedThreads = new LinkedList<>(threads);
        updatedThreads.add(thread);

        return new ThreadHolder(updatedThreads, resultData);
    }


    public ResultData joinAll() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }

        return resultData;
    }
}
