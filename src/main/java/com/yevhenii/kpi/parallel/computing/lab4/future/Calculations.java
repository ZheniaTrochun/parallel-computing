package com.yevhenii.kpi.parallel.computing.lab4.future;

import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.Matrix;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.models.Vector;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;


public class Calculations {
    private final Data data;
    private final Lock lock = new ReentrantLock();

    private Vector D;

    public Calculations(Generator generator) {
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(generator));
    }

    public Calculations() {
        Random rand = new Random();
        this.data = JsonUtils.readInputData()
                .orElseGet(() -> createDataAndWrite(new Generator(rand)));
    }

    public Calculations(Data data) {
        this.data = data;
    }

    private Data createDataAndWrite(Generator gen) {
        Data data = new Data(gen);
        JsonUtils.writeInputData(data);
        return data;
    }

    private CompletableFuture<Vector> first() {
        Supplier<Vector> res = Profilers.profile(
                "A", // "А = В*МС + D*MZ + E*MM"
                () -> {
                    Vector BMC = data.B.multiply(data.MC);
                    Vector EMM = data.E.multiply(data.MM);
                    Vector sum = BMC.add(EMM);
                    lock.lock();
                    Vector DMZ = D.multiply(data.MZ);
                    lock.unlock();
                    return DMZ.add(sum);
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<Vector> second() {
        Supplier<Vector> res = Profilers.profile(
                "D", // "D = В*МZ - E*MM*a"
                () -> {
                    lock.lock();
                    Vector BMZ = data.B.multiply(data.MZ);
                    Vector EMMA = data.E.multiply(data.MM).multiply(data.a);
                    Vector D = BMZ.substruct(EMMA);
                    this.D = D;
                    lock.unlock();
                    return D;
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<Matrix> third() {
        Supplier<Matrix> res = Profilers.profile(
                "MА", // "MА = MD*(MT + MZ) - ME*MM"
                () -> {
                    Matrix MTMZ = data.MT.add(data.MZ);
                    Matrix MDMTMZ = data.MD.multiply(MTMZ);
                    Matrix MEMM = data.ME.multiply(data.MM);
                    return MDMTMZ.substruct(MEMM);
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<Matrix> fourth() {
        Supplier<Matrix> res = Profilers.profile(
                "MG", // "MG = min(D + C)*MD*MT - MZ*ME"
                () -> {
                    Matrix MZME = data.MZ.multiply(data.ME);
                    Matrix MDMT = data.MD.multiply(data.MT);
                    lock.lock();
                    Vector DC = D.add(data.C);
                    lock.unlock();
                    Matrix MDMTDC = MDMT.multiply(DC.min());
                    return MDMTDC.substruct(MZME);
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    public Future<ResultData> start() {
        CompletableFuture<Vector> D = second();
        CompletableFuture<Vector> A = first();
        CompletableFuture<Matrix> MA = third();
        CompletableFuture<Matrix> MG = fourth();

        return CompletableFuture.completedFuture(new ResultData())
                .thenCompose(data -> D.thenApply(data::setD))
                .thenCompose(data -> A.thenApply(data::setA))
                .thenCompose(data -> MA.thenApply(data::setMA))
                .thenCompose(data -> MG.thenApply(data::setMG));
    }
}
