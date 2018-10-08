package pl.gajewski.server.chat.exceptions;

/**
 * @author Gajo
 *         19/05/2015
 */

public class UnauthorizedAccessException extends Exception {

    public UnauthorizedAccessException(String message) {
        super("Unauthorized access from '" + message + "' method");
    }



}
