package pl.gajewski.dirEvent.factory;

import pl.gajewski.WrongFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Klasa OtherFile zarzadza plikami, ktorych nie obslugujemy
 */

public class OtherFile implements MyFiles {

    private String path;

    public OtherFile(String path) {
        this.path = path;
    }

    @Override
    public void useFile() throws WrongFileException {

        File fFile = new File(path);
        if(fFile.delete())
            System.out.println("[Database] File " + path + " is not supported. Deleting..");
        else
            throw new WrongFileException("[Database] File " + path + " can't be deleted.");
    }
}