package pl.gajewski;

/**
 * Created by Lukasz on 08.04.14.
 */

/** my exception */
public class WrongFileException extends Exception {
    public WrongFileException(String message){
        super(message);
    }
}
