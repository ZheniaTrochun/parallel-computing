package com.yevhenii.kpi.parallel.computing.lab6.tasks;

import com.yevhenii.kpi.parallel.computing.lab1.threads.ThreadHolder;
import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.yevhenii.kpi.parallel.computing.utils.Functions.*;
import static com.yevhenii.kpi.parallel.computing.utils.Functions.min;
import static com.yevhenii.kpi.parallel.computing.utils.Functions.substructMatrices;

public class Calculations {
    private final Data data;
    private ResultData resultData = new ResultData();
    private final ForkJoinPool pool = new ForkJoinPool();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private List<Double> D;

    public Calculations(Generator generator) {
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(generator));
    }

    public Calculations() {
        Random rand = new Random();
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(rand::nextDouble));
    }

    private Data createDataAndWrite(Generator gen) {
        Data data = new Data(gen);
        JsonUtils.writeInputData(data);
        return data;
    }

    private Callable<List<Double>> createFirst() {
        return fromSupplier(Profilers.profile("А = В*МС + D*MZ + E*MM",
                () -> {
                    List<Double> BMC = multiply(data.B, data.MC);
                    List<Double> EMM = multiply(data.E, data.MM);
                    List<Double> sum = sumVectors(BMC, EMM);
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    List<Double> DMZ = multiply(D, data.MZ);
                    return sumVectors(DMZ, sum);
                }
        ));
    }

    private Callable<List<Double>> createSecond() {
        return fromSupplier(Profilers.profile("D = В*МZ - E*MM*a",
                () -> {
                    List<Double> BMZ = multiply(data.B, data.MZ);
                    List<Double> EMMA = multiplyByNum(multiply(data.E, data.MM), data.a);
                    List<Double> D = substructVectors(BMZ, EMMA);
                    this.D = D;
                    countDownLatch.countDown();
                    return D;
                }
        ));
    }

    private Callable<List<List<Double>>> createThird() {
        return fromSupplier(Profilers.profile("MА = MD*(MT + MZ) - ME*MM",
                () -> {
                    List<List<Double>> MTMZ = sumMatrices(data.MT, data.MZ);
                    List<List<Double>> MDMTMZ = multiplyMatrices(data.MD, MTMZ);
                    List<List<Double>> MEMM = multiplyMatrices(data.ME, data.MM);
                    return substructMatrices(MDMTMZ, MEMM);
                }
        ));
    }

    private Callable<List<List<Double>>> createFourth() {
        return fromSupplier(Profilers.profile("MG = min(D + C)*MD*MT - MZ*ME",
                () -> {
                    List<List<Double>> MZME = multiplyMatrices(data.MZ, data.ME);
                    List<List<Double>> MDMT = multiplyMatrices(data.MD, data.MT);
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    List<Double> DC = sumVectors(D, data.C);
                    List<List<Double>> MDMTDC = multiplyMatrixByNum(MDMT, min(DC));
                    return substructMatrices(MDMTDC, MZME);
                }
        ));
    }

    public ResultData start() throws ExecutionException, InterruptedException {
        ForkJoinTask<List<Double>> first = pool.submit(createFirst());
        ForkJoinTask<List<Double>> second = pool.submit(createSecond());
        ForkJoinTask<List<List<Double>>> third = pool.submit(createThird());
        ForkJoinTask<List<List<Double>>> fourth = pool.submit(createFourth());

        return resultData.setA(first.get())
                .setD(second.get())
                .setMA(third.get())
                .setMG(fourth.get());
    }

    private <T> Callable<T> fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }
}
