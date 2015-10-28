package data;

import data.figures.FiguresFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lukasz on 2014-06-07.
 */

public class DataLoader {

    private String path;
    private ArrayList<IFigures> figures;

    public DataLoader(String path) {
        this.path = path;
    }

    public ArrayList<IFigures> getFigures() throws IOException, NumberFormatException {
        if(figures == null) {
            File file = new File(path);
            boolean endFlag = true;
            Scanner scanner = new Scanner(file);
            figures = new ArrayList<IFigures>();
            while (scanner.hasNextLine() && endFlag) {
                String line = scanner.nextLine();
                if (line.length() > 70) throw new IOException("Syntax error in file '" + file.toString() + "' at line: " + line);
                if (line.charAt(0) != '*') {     // ignore comments
                    if (line.trim().equals("END"))
                        endFlag = false;
                    else if (line.trim().charAt(0) != '0') {     // ignore 0 figure
                        IFigures gf = getGeometricFigureFromLine(line);
                        figures.add(gf);
                    }
                }
            }
        }
        return figures;
    }

    private IFigures getGeometricFigureFromLine(String line) throws IOException, NumberFormatException {
        String name = line.substring(2,5);
        int id = Integer.parseInt(line.substring(6,10).trim());
        double p1 = Double.parseDouble(line.substring(10, 21).trim());
        double p2 = Double.parseDouble(line.substring(20, 31).trim());
        double p3 = Double.parseDouble(line.substring(30, 41).trim());
        double p4 = Double.parseDouble(line.substring(40, 51).trim());
        double p5 = Double.parseDouble(line.substring(50, 61).trim());
        double p6 = Double.parseDouble(line.substring(60, 70).trim());

        // validate
        IFigures figure = FiguresFactory.getFigure(name, id, p1, p2, p3, p4, p5, p6);
        if(!figure.isValuesOK()) throw new IOException("Wrong values in figure: " + figure.getInfo());

        return figure;
    }

}
