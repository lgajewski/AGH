package pl.gajewski.zad3.semaphore;

/**
 * @author Gajo
 *         22/03/2015
 */

public class BinarySemaphore implements ISemaphore {

    private boolean locked;

    public BinarySemaphore() {
        this.locked = false;
    }

    synchronized public void p() throws InterruptedException {
        while(locked) {
            wait();
        }
        this.locked = true;
        notifyAll();
    }

    synchronized public void v() throws InterruptedException {
        while(!locked) {
            wait();
        }
        this.locked = false;
        notifyAll();
    }

}
