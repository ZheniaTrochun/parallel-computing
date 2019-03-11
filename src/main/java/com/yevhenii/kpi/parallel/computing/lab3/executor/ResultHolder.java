package com.yevhenii.kpi.parallel.computing.lab3.executor;

import com.yevhenii.kpi.parallel.computing.models.ResultData;

import java.util.concurrent.CountDownLatch;

public class ResultHolder {
    private final ResultData resultData;
    private final CountDownLatch resultCountDown;

    public ResultHolder(ResultData resultData, CountDownLatch resultCountDown) {
        this.resultData = resultData;
        this.resultCountDown = resultCountDown;
    }

    public ResultData get() throws InterruptedException {
        resultCountDown.await();
        return resultData;
    }
}
