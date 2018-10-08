import java.io.File;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by Lukasz on 2014-06-09.
 */

public class Config {

    public static boolean isDir(String path) {
        File directory = new File(path);
        return (directory.exists() && directory.isDirectory());
    }

    public static String getExtension(String path) {
        int i = path.lastIndexOf('.');
        if (i <= 0) return "";
        return path.substring(i);
    }

    public static String getFileName(String path) {
        int i = path.lastIndexOf('.');
        int j = path.lastIndexOf(File.separator);
        if (i <= 0 || j <= 0) return "";
        return path.substring(j+1,i);
    }

    public static void printAvailableResources() {
        if(!isDir(Main.resourcesPath)) throw new IllegalArgumentException("Failed to open directory: " + Main.resourcesPath);

        // listing all files from dir
        File fDir = new File(Main.resourcesPath);
        File[] listOfFiles = fDir.listFiles();

        System.out.println("\nEnter integer to take an action: ");

        if (listOfFiles != null) {

            for (File fileFromDir : listOfFiles) {
                String extension = getExtension(fileFromDir.toString());
                if(extension.equals(".dat")) {
                    int key = Main.resources.size() + 1;
                    String value = fileFromDir.getName();
                    System.out.println("(" + key + ") Generate planes from '" + value + "'");
                    Main.resources.put(key, value);
                } else {
                    System.out.println("[Error] Can't load file: " + fileFromDir.toString());
                }
            }

        }
    }

    public static String getTypedResource() throws NoSuchElementException, NumberFormatException {
        Scanner in = new Scanner(System.in);
        int num = in.nextInt();
        if(!Main.resources.containsKey(num)) {
            System.out.println("[Error] Unsupported action: " + num);
            return "UNSUPPORTED";
        }
        return (Main.resourcesPath + File.separator + Main.resources.get(num));
    }

    public static double[] getEnteredValues() throws NumberFormatException {
        System.out.println("Enter (x0,y0,z0). For example: (100,2.3,0)");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();

        if(line.charAt(0) != '(' || line.charAt(line.length()-1) != ')') throw new NumberFormatException("Wrong input");
        line = line.substring(1, line.length()-1);
        String[] valuesFromLine = line.split(",");
        if(valuesFromLine.length != 3) throw new NumberFormatException("Wrong input");

        double[] result = new double[3];
        result[0] = Double.valueOf(valuesFromLine[0]);
        result[1] = Double.valueOf(valuesFromLine[1]);
        result[2] = Double.valueOf(valuesFromLine[2]);

        return result;
    }
}
