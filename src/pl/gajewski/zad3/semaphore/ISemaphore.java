package pl.gajewski.zad3.semaphore;

/**
 * @author Gajo
 *         23/03/2015
 */

public interface ISemaphore {

    public void p() throws InterruptedException;
    public void v() throws InterruptedException;

}
