package com.yevhenii.kpi.parallel.computing.lab1.threads;

import com.yevhenii.kpi.parallel.computing.models.Matrix;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.models.Vector;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.Random;
import java.util.function.Supplier;


public class CalculationThreads {
    private final Data data;
    private ResultData resultData = new ResultData();

    private static final Object MONITOR = new Object();

    public CalculationThreads(Generator generator) {
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(generator));
    }

    public CalculationThreads() {
        Random rand = new Random();
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(new Generator(rand)));
    }

    public CalculationThreads(Data data) {
        this.data = data;
    }

    private Data createDataAndWrite(Generator gen) {
        Data data = new Data(gen);
        JsonUtils.writeInputData(data);
        return data;
    }

    private Runnable createFirst() {
        return fromSupplier(Profilers.profile("A", // "А = В*МС + D*MZ + E*MM"
                () -> {
                    Vector BMC = data.B.multiply(data.MC);
                    Vector EMM = data.E.multiply(data.MM);
                    Vector sum = BMC.add(EMM);
                    synchronized (MONITOR) {
                        if (resultData.getD() == null) {
                            try {
                                MONITOR.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Vector DMZ = resultData.getD().multiply(data.MZ);
                    Vector A = DMZ.add(sum);
                    resultData.setA(A);
                    return A;
                }
        ));
    }

    private Runnable createSecond() {
        return fromSupplier(Profilers.profile("D", // "D = В*МZ - E*MM*a"
                () -> {
                    synchronized (MONITOR) {
                        Vector BMZ = data.B.multiply(data.MZ);
                        Vector EMMA = data.E.multiply(data.MM).multiply(data.a);
                        Vector D = BMZ.substruct(EMMA);
                        resultData.setD(D);
                        MONITOR.notifyAll();
                        return D;
                    }
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
                    return MA;
                }
        ));
    }

    private Runnable createFourth() {
        return fromSupplier(Profilers.profile("MG", // "MG = min(D + C)*MD*MT - MZ*ME"
                () -> {
                    Matrix MZME = data.MZ.multiply(data.ME);
                    Matrix MDMT = data.MD.multiply(data.MT);
                    synchronized (MONITOR) {
                        if (resultData.getD() == null) {
                            try {
                                MONITOR.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Vector DC = resultData.getD().add(data.C);
                    Matrix MDMTDC = MDMT.multiply(DC.min());
                    Matrix MG = MDMTDC.substruct(MZME);
                    resultData.setMG(MG);
                    return MG;
                }
        ));
    }

    public ThreadHolder start() {
        return new ThreadHolder(resultData)
                .start(createSecond())
                .start(createFirst())
                .start(createThird())
                .start(createFourth());
    }

    private <T> Runnable fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }
}
