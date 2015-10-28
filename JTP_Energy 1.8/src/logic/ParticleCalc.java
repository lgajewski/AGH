package logic;

/**
 * Created by Lukasz on 2014-05-12.
 */

public class ParticleCalc implements Calculate {

    @Override
    public double getRelativeSpeed(double e, double m, double c) {

        double mc2 = m*c*c;
        double sqrt1 = mc2/(e+mc2);
        return Math.sqrt(((sqrt1*sqrt1) - 1)*(-1));

    }

    @Override
    public double getMass(String particle) {
        if(particle.equals("electron")) {
            return 9.10938291e-31;
        } else if(particle.equals("proton")) {
            return 1.672621637e-27;
        } else {
            return 0;
        }
    }

    @Override
    public double getEnergyJ(String energyType, double value) {
        double tmp = 1.60217657e-19;
        double multiplier;

        if(energyType.equals("MeV")) {
            multiplier = 1e6;
        } else if(energyType.equals("eV")) {
            multiplier = 1;
        } else if(energyType.equals("keV")) {
            multiplier = 1e3;
        } else if(energyType.equals("GeV")) {
            multiplier = 1e9;
        } else {
            return 0;
        }
        return value*tmp*multiplier;
    }

    @Override
    public double getC() {
        return 3e8;
    }
}