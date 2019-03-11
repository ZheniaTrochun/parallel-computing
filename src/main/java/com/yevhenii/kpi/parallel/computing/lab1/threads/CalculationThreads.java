package com.yevhenii.kpi.parallel.computing.lab1.threads;

import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static com.yevhenii.kpi.parallel.computing.utils.Functions.*;


public class CalculationThreads {
    private final Data data;

    private ResultData resultData = new ResultData();

    private volatile boolean isFinishedD = false;

    public CalculationThreads(Generator generator) {
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(generator));
    }

    public CalculationThreads() {
        Random rand = new Random();
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(rand::nextDouble));
    }

    private Data createDataAndWrite(Generator gen) {
        Data data = new Data(gen);
        JsonUtils.writeInputData(data);
        return data;
    }

    private Runnable createFirst() {
        return fromSupplier(Profilers.profile("А = В*МС + D*MZ + E*MM",
                () -> {
                    List<Double> BMC = multiply(data.B, data.MC);
                    List<Double> EMM = multiply(data.E, data.MM);
                    List<Double> sum = sumVectors(BMC, EMM);
                    waitForD();
                    List<Double> DMZ = multiply(resultData.getD(), data.MZ);
                    List<Double> A = sumVectors(DMZ, sum);
                    resultData.setA(A);
                    return A;
                }
        ));
    }

    private Runnable createSecond() {
        return fromSupplier(Profilers.profile("D = В*МZ - E*MM*a",
                () -> {
                    List<Double> BMZ = multiply(data.B, data.MZ);
                    List<Double> EMMA = multiplyByNum(multiply(data.E, data.MM), data.a);
                    List<Double> D = substructVectors(BMZ, EMMA);
                    resultData.setD(D);
                    setFinishedD();
                    return D;
                }
        ));
    }

    private Runnable createThird() {
        return fromSupplier(Profilers.profile("MА = MD*(MT + MZ) - ME*MM",
                () -> {
                    List<List<Double>> MTMZ = sumMatrices(data.MT, data.MZ);
                    List<List<Double>> MDMTMZ = multiplyMatrices(data.MD, MTMZ);
                    List<List<Double>> MEMM = multiplyMatrices(data.ME, data.MM);
                    List<List<Double>> MA = substructMatrices(MDMTMZ, MEMM);
                    resultData.setMA(MA);
                    return MA;
                }
        ));
    }

    private Runnable createFourth() {
        return fromSupplier(Profilers.profile("MG = min(D + C)*MD*MT - MZ*ME",
                () -> {
                    List<List<Double>> MZME = multiplyMatrices(data.MZ, data.ME);
                    List<List<Double>> MDMT = multiplyMatrices(data.MD, data.MT);
                    waitForD();
                    List<Double> DC = sumVectors(resultData.getD(), data.C);
                    List<List<Double>> MDMTDC = multiplyMatrixByNum(MDMT, min(DC));
                    List<List<Double>> MG = substructMatrices(MDMTDC, MZME);
                    resultData.setMG(MG);
                    return MG;
                }
        ));
    }

    public ThreadHolder start() {
        return new ThreadHolder(resultData)
                .startFirst(createFirst())
                .startSecond(createSecond())
                .startThird(createThird())
                .startFourth(createFourth());
    }

    private void setFinishedD() {
        this.isFinishedD = true;
    }

    private void waitForD() {
        while (!this.isFinishedD) {}
    }

    private <T> Runnable fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }
}
