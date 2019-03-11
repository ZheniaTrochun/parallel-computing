package com.yevhenii.kpi.parallel.computing.lab3.executor;

import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CalculationThreads calculations = new CalculationThreads();
        ResultData res = calculations.start().get();
        JsonUtils.readOutputData().ifPresent(fromFile -> {
            if (!res.equals(fromFile)) {
                System.out.println("NOT MATCHED!!!");
            }
        });
        JsonUtils.writeOutputData(res);
    }
}
