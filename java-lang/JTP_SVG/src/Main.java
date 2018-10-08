import data.DataLoader;
import data.IFigures;
import dom.PlaneGenerator;
import org.w3c.dom.Document;
import svg.SVGCreator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Main {

    // hard-coded paths
    public static String resourcesPath = Paths.get("res").toString();

    // resources
    public static HashMap<Integer, String> resources = new HashMap<Integer, String>();

    public static void main(String[] args) {

        try {
            // GET ACTION FROM USER
            Config.printAvailableResources();

            String file1 = Config.getTypedResource();
            if(!file1.equals("UNSUPPORTED")) {
                double[] values = Config.getEnteredValues();
                String[] axis = { "OX_", "OY_", "OZ_" };
                for (int i = 0; i < 3; i++) {
                    String whatPlane = axis[i] + values[i];
                    // START GENERATION
                    DataLoader geo = new DataLoader(file1);       // create object from file .dat
                    ArrayList<IFigures> geoFigures = geo.getFigures(); // get arraylist of figures

                    System.out.println("\nLoaded figures from '" + file1 + "'");
                    generatePlanes(geoFigures, whatPlane, axis[i] + Config.getFileName(file1));       // generate SVG file
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] There is no file");
        } catch (IOException e) {
            System.out.println("[ERROR]" + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("[ERROR] ParserConfigurationException");
        } catch (TransformerException e) {
            System.out.println("[ERROR] TransformerException");
        } catch (NoSuchElementException e) {
            System.out.println("[ERROR] NoSuchElementException: " + e.getMessage());
        }

    }

    private static void generatePlanes(ArrayList<IFigures> figures, String whatPlane,  String output) throws ParserConfigurationException,
        TransformerException, FileNotFoundException {

        File svgDir = new File("svg");
        if(!svgDir.exists()) svgDir.mkdir();

        PlaneGenerator plane = new PlaneGenerator(whatPlane, figures);
        Document doc = plane.generateDoc();    // get finalized DOM Document
        SVGCreator.create(doc, Paths.get("svg", output+".svg").toString());

    }
}
