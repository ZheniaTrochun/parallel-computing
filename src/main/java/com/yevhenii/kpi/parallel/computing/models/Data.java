package com.yevhenii.kpi.parallel.computing.models;

import com.yevhenii.kpi.parallel.computing.utils.Generator;

import java.util.Objects;

public class Data {
    private static final int SIZE = 500;

    public Vector B;
    public Vector C;
    public Vector E;

    public Matrix MC;
    public Matrix MZ;
    public Matrix MM;
    public Matrix MD;
    public Matrix MT;
    public Matrix ME;

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

    public Vector getB() {
        return B;
    }

    public void setB(Vector b) {
        B = b;
    }

    public Vector getC() {
        return C;
    }

    public void setC(Vector c) {
        C = c;
    }

    public Vector getE() {
        return E;
    }

    public void setE(Vector e) {
        E = e;
    }

    public Matrix getMC() {
        return MC;
    }

    public void setMC(Matrix MC) {
        this.MC = MC;
    }

    public Matrix getMZ() {
        return MZ;
    }

    public void setMZ(Matrix MZ) {
        this.MZ = MZ;
    }

    public Matrix getMM() {
        return MM;
    }

    public void setMM(Matrix MM) {
        this.MM = MM;
    }

    public Matrix getMD() {
        return MD;
    }

    public void setMD(Matrix MD) {
        this.MD = MD;
    }

    public Matrix getMT() {
        return MT;
    }

    public void setMT(Matrix MT) {
        this.MT = MT;
    }

    public Matrix getME() {
        return ME;
    }

    public void setME(Matrix ME) {
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
