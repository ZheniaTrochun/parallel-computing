package com.yevhenii.kpi.parallel.computing.lab1.threads;

import com.yevhenii.kpi.parallel.computing.models.ResultData;

public class ThreadHolder {
    private Thread first;
    private Thread second;
    private Thread third;
    private Thread fourth;

    private ResultData resultData;

    public ThreadHolder(ResultData resultData) {
        this.resultData = resultData;
    }

    public ThreadHolder startFirst(Runnable calculations) {
        first = new Thread(calculations);
        first.start();
        return this;
    }

    public ThreadHolder startSecond(Runnable calculations) {
        second = new Thread(calculations);
        second.start();
        return this;
    }

    public ThreadHolder startThird(Runnable calculations) {
        third = new Thread(calculations);
        third.start();
        return this;
    }

    public ThreadHolder startFourth(Runnable calculations) {
        fourth = new Thread(calculations);
        fourth.start();
        return this;
    }

    public ResultData joinAll() throws InterruptedException {
        first.join();
        second.join();
        third.join();
        fourth.join();

        return resultData;
    }
}
