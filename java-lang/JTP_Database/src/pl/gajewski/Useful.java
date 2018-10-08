package pl.gajewski;

import java.io.File;

/**
 * Created by Lukasz on 09.04.14.
 *
 * Here are some useful methods.
 *
 */


public class Useful {

    public static void deleteDir(String path, boolean removeParent) throws WrongFileException {
        File directory = new File(path);
        if(!isDir(path)) throw new WrongFileException("No directory " + path);
        File[] files = directory.listFiles();
        if(files != null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteDir(f.toString(), true);
                } else {
                    if(f.delete())
                        System.out.println("\t => " + f.toString() + " deleted.");
                    else
                        throw new WrongFileException("Failed to delete file: " + f.toString());
                }
            }
        }
        if(removeParent && directory.delete())
            System.out.println("[Database] Directory " + path + " has been removed successfully");
        if(!removeParent)
            System.out.println("[Database] Directory " + path + " has been cleared successfully");
    }

    public static boolean isDir(String path) {
        File directory = new File(path);
        return (directory.exists() && directory.isDirectory());
    }

    public static String getExtension(String path) throws WrongFileException {
        int i = path.lastIndexOf('.');
        int j = path.lastIndexOf('\\');
        // rzuca wyjatkiem, gdy sciezka jest niepoprawna
        if (i <= 0 || j <= 0) throw new WrongFileException("[Database] File " + path + " is incorrect.");
        return path.substring(i);
    }

    public static String getFileName(String path) throws WrongFileException {
        int i = path.lastIndexOf('.');
        int j = path.lastIndexOf('\\');
        // rzuca wyjatkiem, gdy sciezka jest niepoprawna
        if (i <= 0 || j <= 0) throw new WrongFileException("[Database] File " + path + " is incorrect.");
        return path.substring(j+1, i);
    }

}
