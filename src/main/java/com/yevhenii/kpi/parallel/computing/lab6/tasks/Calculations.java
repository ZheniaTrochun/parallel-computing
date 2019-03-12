package com.yevhenii.kpi.parallel.computing.lab6.tasks;

import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.Matrix;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.models.Vector;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;


public class Calculations {
    private final Data data;
    private ResultData resultData = new ResultData();
    private final ForkJoinPool pool = new ForkJoinPool();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private Vector D;

    public Calculations(Generator generator) {
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(generator));
    }

    public Calculations(Data data) {
        this.data = data;
    }

    public Calculations() {
        Random rand = new Random();
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(new Generator(rand)));
    }

    private Data createDataAndWrite(Generator gen) {
        Data data = new Data(gen);
        JsonUtils.writeInputData(data);
        return data;
    }

    private Callable<Vector> createFirst() {
        return fromSupplier(Profilers.profile("A", // "А = В*МС + D*MZ + E*MM"
                () -> {
                    Vector BMC = data.B.multiply(data.MC);
                    Vector EMM = data.E.multiply(data.MM);
                    Vector sum = BMC.add(EMM);
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    Vector DMZ = D.multiply(data.MZ);
                    return DMZ.add(sum);
                }
        ));
    }

    private Callable<Vector> createSecond() {
        return fromSupplier(Profilers.profile("D", // "D = В*МZ - E*MM*a"
                () -> {
                    Vector BMZ = data.B.multiply(data.MZ);
                    Vector EMMA = data.E.multiply(data.MM).multiply(data.a);
                    Vector D = BMZ.substruct(EMMA);
                    this.D = D;
                    countDownLatch.countDown();
                    return D;
                }
        ));
    }

    private Callable<Matrix> createThird() {
        return fromSupplier(Profilers.profile("MА", // "MА = MD*(MT + MZ) - ME*MM"
                () -> {
                    Matrix MTMZ = data.MT.add(data.MZ);
                    Matrix MDMTMZ = data.MD.multiply(MTMZ);
                    Matrix MEMM = data.ME.multiply(data.MM);
                    return MDMTMZ.substruct(MEMM);
                }
        ));
    }

    private Callable<Matrix> createFourth() {
        return fromSupplier(Profilers.profile("MG", // "MG = min(D + C)*MD*MT - MZ*ME"
                () -> {
                    Matrix MZME = data.MZ.multiply(data.ME);
                    Matrix MDMT = data.MD.multiply(data.MT);
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    Vector DC = D.add(data.C);
                    Matrix MDMTDC = MDMT.multiply(DC.min());
                    return MDMTDC.substruct(MZME);
                }
        ));
    }

    public ResultData start() throws ExecutionException, InterruptedException {
        ForkJoinTask<Vector> first = pool.submit(createFirst());
        ForkJoinTask<Vector> second = pool.submit(createSecond());
        ForkJoinTask<Matrix> third = pool.submit(createThird());
        ForkJoinTask<Matrix> fourth = pool.submit(createFourth());

        return resultData.setA(first.get())
                .setD(second.get())
                .setMA(third.get())
                .setMG(fourth.get());
    }

    private <T> Callable<T> fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }
}
