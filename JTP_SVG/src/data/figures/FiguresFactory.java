package data.figures;

import data.IFigures;
import java.util.NoSuchElementException;

/**
 * Created by Lukasz on 2014-06-07.
 */

public class FiguresFactory {

    public static IFigures getFigure(String name, int id, double p1, double p2, double p3, double p4,
                                     double p5, double p6) throws NoSuchElementException {

        if(name.equals("SPH")) {
            return new SPH(id, p1, p2, p3, p4);
        } else if(name.equals("RPP")) {
            return new RPP(id, p1, p2, p3, p4, p5, p6);
        } else {
            throw new NoSuchElementException("Unsupported figure: " + name);
        }

    }

}
