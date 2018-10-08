package pl.gajewski.dirEvent.factory;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Kopiuje pliki z folderu obserwowanego do folderu bin i odpowiedniego
 * katalogu uzytkownika
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import pl.gajewski.Useful;
import pl.gajewski.WrongFileException;
import pl.gajewski.Main;

public class MoveToDirFile implements MyFiles {

    private String path;

    public MoveToDirFile(String path) {
        this.path = path;
    }

    @Override
    public void useFile() throws WrongFileException {

        String bin = Main.bin;
        String tempDir = Main.tempDir;

        String fileName = Useful.getFileName(path);
        String extension = Useful.getExtension(path);

        File fDir = new File(bin + fileName);
        File fFile = new File(path);
        Path target;

        // sprawdza czy folder uzytkownika istnieje
        if(fDir.exists() && fDir.isDirectory()) {
            // kopiuje plik do katalogu uzytkownika
            try {
                target = Paths.get(bin, fileName, fileName + extension);
                Files.copy(fFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[Database] Copied file: " + path + " to " + target.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // przenosi plik do katalogu tempDir
            try {
                target = Paths.get(tempDir, fileName + extension);
                Files.move(fFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[Database] Failed to move file " + path
                        + ". Directory doesn't exist. " + "Moving it to temp directory.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}