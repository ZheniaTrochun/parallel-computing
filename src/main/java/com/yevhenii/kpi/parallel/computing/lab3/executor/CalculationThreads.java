package com.yevhenii.kpi.parallel.computing.lab3.executor;

import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.Matrix;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.models.Vector;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;


public class CalculationThreads {
    private final Data data;

    private ResultData resultData = new ResultData();

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final CountDownLatch resultCountDown = new CountDownLatch(4);
    private final Executor executor = Executors.newCachedThreadPool();

    public CalculationThreads(Generator generator) {
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(generator));
    }

    public CalculationThreads(Data data) {
        this.data = data;
    }

    public CalculationThreads() {
        Random rand = new Random();
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(new Generator(rand)));
    }

    private Data createDataAndWrite(Generator gen) {
        Data data = new Data(gen);
        JsonUtils.writeInputData(data);
        return data;
    }

    private Runnable createFirst() {
        return fromSupplier(Profilers.profile("А", //"А = В*МС + D*MZ + E*MM",
                () -> {
                    Vector BMC = data.B.multiply(data.MC);
                    Vector EMM = data.E.multiply(data.MM);
                    Vector sum = BMC.add(EMM);
                    try {
                        countDownLatch.await();
                        Vector DMZ = resultData.getD().multiply(data.MZ);
                        Vector A = DMZ.add(sum);
                        resultData.setA(A);
                        return A;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } finally {
                        resultCountDown.countDown();
                    }
                }
        ));
    }

    private Runnable createSecond() {
        return fromSupplier(Profilers.profile("D", // "D = В*МZ - E*MM*a",
                () -> {
                    Vector BMZ = data.B.multiply(data.MZ);
                    Vector EMMA = data.E.multiply(data.MM).multiply(data.a);
                    Vector D = BMZ.substruct(EMMA);
                    resultData.setD(D);
                    countDownLatch.countDown();
                    resultCountDown.countDown();
                    return D;
                }
        ));
    }

    private Runnable createThird() {
        return fromSupplier(Profilers.profile("MА", // "MА = MD*(MT + MZ) - ME*MM"
                () -> {
                    Matrix MTMZ = data.MT.add(data.MZ);
                    Matrix MDMTMZ = data.MD.multiply(MTMZ);
                    Matrix MEMM = data.ME.multiply(data.MM);
                    Matrix MA = MDMTMZ.substruct(MEMM);
                    resultData.setMA(MA);
                    resultCountDown.countDown();
                    return MA;
                }
        ));
    }

    private Runnable createFourth() {
        return fromSupplier(Profilers.profile("MG", // MG = min(D + C)*MD*MT - MZ*ME
                () -> {
                    Matrix MZME = data.MZ.multiply(data.ME);
                    Matrix MDMT = data.MD.multiply(data.MT);
                    try {
                        countDownLatch.await();
                        Vector DC = resultData.getD().add(data.C);
                        Matrix MDMTDC = MDMT.multiply(DC.min());
                        Matrix MG = MDMTDC.substruct(MZME);
                        resultData.setMG(MG);
                        return MG;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } finally {
                        resultCountDown.countDown();
                    }
                }
        ));
    }

    public ResultHolder start() {
        executor.execute(createFirst());
        executor.execute(createSecond());
        executor.execute(createThird());
        executor.execute(createFourth());

        return new ResultHolder(resultData, resultCountDown);
    }

    private <T> Runnable fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }
}
