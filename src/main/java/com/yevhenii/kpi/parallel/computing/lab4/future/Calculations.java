package com.yevhenii.kpi.parallel.computing.lab4.future;

import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.yevhenii.kpi.parallel.computing.utils.Functions.*;
import static com.yevhenii.kpi.parallel.computing.utils.Functions.substructMatrices;

public class Calculations {
    private final Data data;
    private final Lock lock = new ReentrantLock();

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

    private CompletableFuture<List<Double>> first() {
        Supplier<List<Double>> res = Profilers.profile(
                "А = В*МС + D*MZ + E*MM",
                () -> {
                    List<Double> BMC = multiply(data.B, data.MC);
                    List<Double> EMM = multiply(data.E, data.MM);
                    List<Double> sum = sumVectors(BMC, EMM);
                    lock.lock();
                    lock.unlock();
                    List<Double> DMZ = multiply(D, data.MZ);
                    return sumVectors(DMZ, sum);
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<List<Double>> second() {
        Supplier<List<Double>> res = Profilers.profile(
                "D = В*МZ - E*MM*a",
                () -> {
                    lock.lock();
                    List<Double> BMZ = multiply(data.B, data.MZ);
                    List<Double> EMMA = multiplyByNum(multiply(data.E, data.MM), data.a);
                    List<Double> D = substructVectors(BMZ, EMMA);
                    this.D = D;
                    lock.unlock();
                    return D;
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<List<List<Double>>> third() {
        Supplier<List<List<Double>>> res = Profilers.profile(
                "MА = MD*(MT + MZ) - ME*MM",
                () -> {
                    List<List<Double>> MTMZ = sumMatrices(data.MT, data.MZ);
                    List<List<Double>> MDMTMZ = multiplyMatrices(data.MD, MTMZ);
                    List<List<Double>> MEMM = multiplyMatrices(data.ME, data.MM);
                    return substructMatrices(MDMTMZ, MEMM);
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<List<List<Double>>> fourth() {
        Supplier<List<List<Double>>> res = Profilers.profile(
                "MG = min(D + C)*MD*MT - MZ*ME",
                () -> {
                    List<List<Double>> MZME = multiplyMatrices(data.MZ, data.ME);
                    List<List<Double>> MDMT = multiplyMatrices(data.MD, data.MT);
                    lock.lock();
                    lock.unlock();
                    List<Double> DC = sumVectors(D, data.C);
                    List<List<Double>> MDMTDC = multiplyMatrixByNum(MDMT, min(DC));
                    return substructMatrices(MDMTDC, MZME);
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    public Future<ResultData> start() {
        CompletableFuture<List<Double>> D = second();
        CompletableFuture<List<Double>> A = first();
        CompletableFuture<List<List<Double>>> MA = third();
        CompletableFuture<List<List<Double>>> MG = fourth();

        return CompletableFuture.completedFuture(new ResultData())
                .thenCompose(data -> D.thenApply(data::setD))
                .thenCompose(data -> A.thenApply(data::setA))
                .thenCompose(data -> MA.thenApply(data::setMA))
                .thenCompose(data -> MG.thenApply(data::setMG));
    }
}