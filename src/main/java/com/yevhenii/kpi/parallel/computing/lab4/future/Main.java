package com.yevhenii.kpi.parallel.computing.lab4.future;

import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Calculations calculations = new Calculations();
        ResultData res = calculations.start().get();
        JsonUtils.readOutputData().ifPresent(fromFile -> {
            if (!res.equals(fromFile)) {
                System.out.println("NOT MATCHED!!!");
            }
        });
        JsonUtils.writeOutputData(res);
    }
}
