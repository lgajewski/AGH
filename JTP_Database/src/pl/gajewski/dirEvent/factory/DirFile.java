package pl.gajewski.dirEvent.factory;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Klasa DirFile obsluguje katalogi wrzucane do katalogu sledzonego
 * Usuwa katalogi wraz z ich zawartoscia
 */

import pl.gajewski.Useful;
import pl.gajewski.WrongFileException;

public class DirFile implements MyFiles {

    private String path;

    public DirFile(String path) {
        this.path = path;
    }

    @Override
    public void useFile() {
        try {
            Useful.deleteDir(path, true);
        } catch (WrongFileException e) {
            e.printStackTrace();
        }
    }
}