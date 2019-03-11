package com.yevhenii.kpi.parallel.computing.models;

import java.util.List;
import java.util.Objects;

public class ResultData {
    private List<Double> D;
    private List<Double> A;
    private List<List<Double>> MA;
    private List<List<Double>> MG;

    public ResultData() {
    }

    public ResultData(List<Double> D, List<Double> A, List<List<Double>> MA, List<List<Double>> MG) {
        D = D;
        A = A;
        this.MA = MA;
        this.MG = MG;
    }

    public List<Double> getD() {
        return D;
    }

    public ResultData setD(List<Double> d) {
        D = d;
        return this;
    }

    public List<Double> getA() {
        return A;
    }

    public ResultData setA(List<Double> a) {
        A = a;
        return this;
    }

    public List<List<Double>> getMA() {
        return MA;
    }

    public ResultData setMA(List<List<Double>> MA) {
        this.MA = MA;
        return this;
    }

    public List<List<Double>> getMG() {
        return MG;
    }

    public ResultData setMG(List<List<Double>> MG) {
        this.MG = MG;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultData)) return false;
        ResultData that = (ResultData) o;
        return Objects.equals(getD(), that.getD()) &&
                Objects.equals(getA(), that.getA()) &&
                Objects.equals(getMA(), that.getMA()) &&
                Objects.equals(getMG(), that.getMG());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getD(), getA(), getMA(), getMG());
    }
}
