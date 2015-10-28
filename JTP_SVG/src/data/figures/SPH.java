package data.figures;

import data.IFigures;

/**
 * Created by Lukasz on 2014-06-07.
 */

public class SPH implements IFigures {

    private int id;
    private double p1;
    private double p2;
    private double p3;
    private double p4;

    public SPH(int id, double p1, double p2, double p3, double p4) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    public int getId() { return id; }
    public double getP1() { return p1; }
    public double getP2() { return p2; }
    public double getP3() { return p3; }
    public double getP4() { return p4; }
    // there is no p5,p6, but getters is needed because of interface
    public double getP5() { return 0; }
    public double getP6() { return 0; }
    public boolean isValuesOK() { return (p4 > 0); }
    public String getName() { return "SPH"; }
    public String getInfo() { return ("SPH: " + p1 + " " + p2 + " " + p3 + " " + p4); }
}
