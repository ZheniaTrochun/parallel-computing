package com.yevhenii.kpi.parallel.computing.models;

import com.yevhenii.kpi.parallel.computing.utils.Generator;

import java.util.List;
import java.util.Objects;

public class Data {
    private static final int SIZE = 1000;

    public List<Double> B;
    public List<Double> C;
    public List<Double> E;

    public List<List<Double>> MC;
    public List<List<Double>> MZ;
    public List<List<Double>> MM;
    public List<List<Double>> MD;
    public List<List<Double>> MT;
    public List<List<Double>> ME;

    public double a;

    public Data() {
    }

    public Data(Generator generator) {
        a = generator.getDouble();

        B = generator.getVector(SIZE);
        C = generator.getVector(SIZE);
        E = generator.getVector(SIZE);

        MC = generator.getMatrix(SIZE);
        MZ = generator.getMatrix(SIZE);
        MM = generator.getMatrix(SIZE);
        MD = generator.getMatrix(SIZE);
        MT = generator.getMatrix(SIZE);
        ME = generator.getMatrix(SIZE);
    }

    public static int getSIZE() {
        return SIZE;
    }

    public List<Double> getB() {
        return B;
    }

    public void setB(List<Double> b) {
        B = b;
    }

    public List<Double> getC() {
        return C;
    }

    public void setC(List<Double> c) {
        C = c;
    }

    public List<Double> getE() {
        return E;
    }

    public void setE(List<Double> e) {
        E = e;
    }

    public List<List<Double>> getMC() {
        return MC;
    }

    public void setMC(List<List<Double>> MC) {
        this.MC = MC;
    }

    public List<List<Double>> getMZ() {
        return MZ;
    }

    public void setMZ(List<List<Double>> MZ) {
        this.MZ = MZ;
    }

    public List<List<Double>> getMM() {
        return MM;
    }

    public void setMM(List<List<Double>> MM) {
        this.MM = MM;
    }

    public List<List<Double>> getMD() {
        return MD;
    }

    public void setMD(List<List<Double>> MD) {
        this.MD = MD;
    }

    public List<List<Double>> getMT() {
        return MT;
    }

    public void setMT(List<List<Double>> MT) {
        this.MT = MT;
    }

    public List<List<Double>> getME() {
        return ME;
    }

    public void setME(List<List<Double>> ME) {
        this.ME = ME;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;
        Data data = (Data) o;
        return Double.compare(data.getA(), getA()) == 0 &&
                Objects.equals(getB(), data.getB()) &&
                Objects.equals(getC(), data.getC()) &&
                Objects.equals(getE(), data.getE()) &&
                Objects.equals(getMC(), data.getMC()) &&
                Objects.equals(getMZ(), data.getMZ()) &&
                Objects.equals(getMM(), data.getMM()) &&
                Objects.equals(getMD(), data.getMD()) &&
                Objects.equals(getMT(), data.getMT()) &&
                Objects.equals(getME(), data.getME());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getB(), getC(), getE(), getMC(), getMZ(), getMM(), getMD(), getMT(), getME(), getA());
    }
}
