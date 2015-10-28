package data.figures;

import data.IFigures;

/**
 * Created by Lukasz on 2014-06-07.
 */

public class RPP implements IFigures {


    private int id;
    private double p1;
    private double p2;
    private double p3;
    private double p4;
    private double p5;
    private double p6;

    public RPP(int id, double p1, double p2, double p3, double p4, double p5, double p6) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
    }

    public int getId() { return id; }
    public double getP1() { return p1; }
    public double getP2() { return p2; }
    public double getP4() { return p4; }
    public double getP3() { return p3; }
    public double getP5() { return p5; }
    public double getP6() { return p6; }
    public boolean isValuesOK() { return !(p1 >= p2 || p3 >= p4 || p5 >= p6); }
    public String getName() { return "RPP"; }
    public String getInfo() { return ("RPP: " + p1 + " " + p2 + " " + p3 + " " + p4 + " " + p5 + " " + p6);  }
}
