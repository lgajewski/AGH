package pl.gajewski.dirEvent;

import pl.gajewski.Useful;
import pl.gajewski.WrongFileException;
import pl.gajewski.dirEvent.factory.*;
import java.io.File;

/**
 * Created by Lukasz on 08.04.14.
 */

public class FilesFactory {

    public static MyFiles createFile(String path) throws WrongFileException {

        File f = new File(path);

        if (!f.exists()) {
            throw new WrongFileException("[Database] File " + path + " is incorrect.");
        } else if (f.isDirectory()) {
            return new DirFile(path);
        } else {

            String extension = Useful.getExtension(path).toLowerCase();

            if (extension.equals(".info")) return new InfoFile(path);
            else if (extension.equals(".txt") || extension.equals(".jpg")) return new MoveToDirFile(path);
            else if (extension.equals(".ser")) return new SerFile(path);
            else return new OtherFile(path);

        }
    }
}