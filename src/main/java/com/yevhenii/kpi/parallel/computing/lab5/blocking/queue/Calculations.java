package com.yevhenii.kpi.parallel.computing.lab5.blocking.queue;

import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.Matrix;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.models.Vector;
import com.yevhenii.kpi.parallel.computing.profiling.Profilers;
import com.yevhenii.kpi.parallel.computing.utils.Generator;
import com.yevhenii.kpi.parallel.computing.utils.JsonUtils;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;


public class Calculations {
    private final Data data;
    private final BlockingQueue<Vector> queue = new ArrayBlockingQueue<>(2);

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

    private CompletableFuture<Vector> first() {
        Supplier<Vector> res = Profilers.profile(
                "А = В*МС + D*MZ + E*MM",
                () -> {
                    Vector BMC = data.B.multiply(data.MC);
                    Vector EMM = data.E.multiply(data.MM);
                    Vector sum = BMC.add(EMM);
                    try {
                        Vector DMZ = queue.take().multiply(data.MZ);
                        return DMZ.add(sum);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<Vector> second() {
        Supplier<Vector> res = Profilers.profile(
                "D = В*МZ - E*MM*a",
                () -> {
                    Vector BMZ = data.B.multiply(data.MZ);
                    Vector EMMA = data.E.multiply(data.MM).multiply(data.a);
                    Vector D = BMZ.substruct(EMMA);
                    queue.add(D);
                    queue.add(D);
                    return D;
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    private CompletableFuture<Matrix> third() {
        Supplier<Matrix> res = Profilers.profile(
                "MА = MD*(MT + MZ) - ME*MM",
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
                "MG = min(D + C)*MD*MT - MZ*ME",
                () -> {
                    Matrix MZME = data.MZ.multiply(data.ME);
                    Matrix MDMT = data.MD.multiply(data.MT);
                    try {
                        Vector DC = queue.take().add(data.C);
                        Matrix MDMTDC = MDMT.multiply(DC.min());
                        return MDMTDC.substruct(MZME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
        );

        return CompletableFuture.supplyAsync(res);
    }

    public Future<ResultData> start() {
        CompletableFuture<Vector> A = first();
        CompletableFuture<Vector> D = second();
        CompletableFuture<Matrix> MA = third();
        CompletableFuture<Matrix> MG = fourth();

        return CompletableFuture.completedFuture(new ResultData())
                .thenCompose(data -> D.thenApply(data::setD))
                .thenCompose(data -> A.thenApply(data::setA))
                .thenCompose(data -> MA.thenApply(data::setMA))
                .thenCompose(data -> MG.thenApply(data::setMG));
    }
}
