package logic;

/**
 * Created by Lukasz on 2014-05-12.
 */

public interface Calculate {
    double getRelativeSpeed(double e, double m, double c);
    double getMass(String particle);
    double getEnergyJ(String energyType, double value);
    double getC();
}

