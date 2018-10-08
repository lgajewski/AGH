package pl.gajewski.dirEvent.factory;

import pl.gajewski.WrongFileException;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Interfejs plikow
 */

public interface MyFiles {
    void useFile() throws WrongFileException;
}
