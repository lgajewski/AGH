package pl.gajewski.zad3.semaphore;

/**
 * @author Gajo
 *         23/03/2015
 */

public class CountingSemaphore implements ISemaphore {

    private int count = 0;
    private int max = 0;

    public CountingSemaphore(int init, int max) {
        if(max <= 0) {
            throw new IllegalStateException("[error] incorrect count");
        }

        this.count = init;
        this.max = max;

    }

    synchronized public void p() throws InterruptedException {
        while(count == 0) {
            wait();
        }
        count--;
        notifyAll();
    }

    synchronized public void v() throws InterruptedException {
        while(count >= max) {
            wait();
        }
        count++;
        notifyAll();
    }

}
