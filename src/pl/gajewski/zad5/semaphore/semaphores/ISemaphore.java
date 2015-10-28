package pl.gajewski.zad5.semaphore.semaphores;

/**
 * @author Gajo
 *         23/03/2015
 */

public interface ISemaphore {

    void p() throws InterruptedException;
    void v() throws InterruptedException;

}
