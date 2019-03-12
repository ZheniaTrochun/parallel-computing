package com.yevhenii.kpi.parallel.computing.models;

import java.util.Objects;

public class ResultData {
    private Vector D;
    private Vector A;
    private Matrix MA;
    private Matrix MG;

    public ResultData() {
    }

    public ResultData(Vector D, Vector A, Matrix MA, Matrix MG) {
        this.D = D;
        this.A = A;
        this.MA = MA;
        this.MG = MG;
    }

    public Vector getD() {
        return D;
    }

    public ResultData setD(Vector d) {
        D = d;
        return this;
    }

    public Vector getA() {
        return A;
    }

    public ResultData setA(Vector a) {
        A = a;
        return this;
    }

    public Matrix getMA() {
        return MA;
    }

    public ResultData setMA(Matrix MA) {
        this.MA = MA;
        return this;
    }

    public Matrix getMG() {
        return MG;
    }

    public ResultData setMG(Matrix MG) {
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
