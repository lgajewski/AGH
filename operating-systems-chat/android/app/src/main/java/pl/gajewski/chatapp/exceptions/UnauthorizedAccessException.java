package pl.gajewski.chatapp.exceptions;

/**
 * Created by Lukasz on 12/05/2015.
 */
public class UnauthorizedAccessException extends Exception {

    public UnauthorizedAccessException(Class aClass) {
        super(aClass.getName() + ": unauthorized access");
    }

    public UnauthorizedAccessException(Class aClass, String s) {
        super(aClass.getName() + ": unauthorized access - " + s);
    }
}
