package dom;

import data.IFigures;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Created by Lukasz on 2014-06-08.
 */

public class PlaneGenerator {

    private ArrayList<IFigures> figures;
    private String whatPlane;
    private double argument;
    private DocManager docManager;

    // viewbox
    private double x_min;
    private double x_max;
    private double y_min;
    private double y_max;

    // point
    private double point = 0.1;

    public PlaneGenerator(String whatPlane, ArrayList<IFigures> figures) throws NoSuchElementException {
        this.figures = figures;
        this.whatPlane = whatPlane.substring(0, 2);
        this.argument = Double.valueOf(whatPlane.substring(3));
        if (!(this.whatPlane.equals("OX") || this.whatPlane.equals("OY") || this.whatPlane.equals("OZ")))
            throw new NoSuchElementException("No such plane: " + whatPlane);
    }

    public Document generateDoc() throws ParserConfigurationException, TransformerException {
        docManager = new DocManager();

        for (IFigures figure : figures) {
            if(figure.getName().equals("RPP")) {
                addRectangle(figure);
            } else if(figure.getName().equals("SPH")) {
                addCircle(figure);
            }
        }
        docManager.setStrokeAndRadius((x_max-x_min)/100, point/100);          // lottery :)
        docManager.setViewBox(x_min + " " + y_min + " " + (x_max-x_min) + " " + (y_max-y_min));
        return docManager.getDoc();
    }

    private void addRectangle(IFigures figure) {
        double width=0, height=0, x=0, y=0;
        boolean flag = true;
        if(whatPlane.equals("OZ")) {
            width = figure.getP2() - figure.getP1();
            height = figure.getP4() - figure.getP3();
            x=figure.getP1();
            y=figure.getP3();
            // check Zmin <= argument <= Zmax
            if(figure.getP6() < argument || figure.getP5() > argument) {
                flag = false;
            } else {
                // view box
                if(figure.getP2() > x_max) x_max = figure.getP2();
                if(figure.getP4() > y_max) y_max = figure.getP4();
            }
        } else if(whatPlane.equals("OY")) {
            width = figure.getP2() - figure.getP1();
            height = figure.getP6() - figure.getP5();
            x=figure.getP1();
            y=figure.getP5();
            // check Ymin <= argument <= Ymax
            if(figure.getP4() < argument || figure.getP3() > argument) {
                flag = false;
            } else {
                // view box
                if(figure.getP2() > x_max) x_max = figure.getP2();
                if(figure.getP6() > y_max) y_max = figure.getP6();
            }
        } else if(whatPlane.equals("OX")) {
            width = figure.getP4() - figure.getP3();
            height = figure.getP6() - figure.getP5();
            x=figure.getP3();
            y=figure.getP5();
            // check Xmin <= argument <= Xmax
            if(figure.getP2() < argument || figure.getP1() > argument) {
                flag = false;
            } else {
                if(figure.getP4() > x_max) x_max = figure.getP4();
                if(figure.getP6() > y_max) y_max = figure.getP6();
            }
        }
        if(flag) {
            // viewbox
            if(x < x_min) x_min = x;
            if(y < y_min) y_min = y;
            Element rectangle = docManager.getDoc().createElement("rect");
            rectangle.setAttribute("id", String.valueOf(figure.getId()));
            rectangle.setAttribute("x", String.valueOf(x));
            rectangle.setAttribute("y", String.valueOf(y));
            rectangle.setAttribute("width", String.valueOf(width));
            rectangle.setAttribute("height", String.valueOf(height));
            rectangle.setAttribute("stroke", "black");
            rectangle.setAttribute("fill", "white");
            rectangle.setAttribute("fill-opacity", "0.0");
            docManager.addFigure(rectangle);
        }
    }
    private void addCircle(IFigures figure) {
        // check -r <= argument <= r
        if(argument >= figure.getP3() - figure.getP4() && argument <= figure.getP3() + figure.getP4()) {
            double cx = 0, cy = 0;
            // calculate new radius: x = sqrt(r*r-a*a);
            double distFromCircleCenter = Math.abs(figure.getP3() - argument);
            double r = Math.sqrt(figure.getP4() * figure.getP4() - distFromCircleCenter * distFromCircleCenter);
            if(r==0) r = point;     // only for viewbox
            if (whatPlane.equals("OZ")) {
                cx = figure.getP1();
                cy = figure.getP2();
            } else if (whatPlane.equals("OY")) {
                cx = figure.getP1();
                cy = figure.getP3();
            } else if (whatPlane.equals("OX")) {
                cx = figure.getP2();
                cy = figure.getP3();
            }
            // viewbox
            if (cx - r < x_min) x_min = cx - r;
            if (cx + r > x_max) x_max = cx + r;
            if (cy - r < y_min) y_min = cy - r;
            if (cy + r > y_max) y_max = cy + r;
            if(r==point) r = 0;
            Element circle = docManager.getDoc().createElement("circle");
            circle.setAttribute("id", String.valueOf(figure.getId()));
            circle.setAttribute("cx", String.valueOf(cx));
            circle.setAttribute("cy", String.valueOf(cy));
            circle.setAttribute("r", String.valueOf(r));
            circle.setAttribute("stroke", "black");
            circle.setAttribute("fill", "white");
            circle.setAttribute("fill-opacity", "0.0");
            docManager.addFigure(circle);
        }
    }
}
