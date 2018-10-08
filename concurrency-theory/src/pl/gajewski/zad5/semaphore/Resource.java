package pl.gajewski.zad5.semaphore;


import pl.gajewski.zad5.semaphore.semaphores.BinarySemaphore;
import pl.gajewski.zad5.semaphore.semaphores.ISemaphore;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Resource {

    private String data;

    private final ISemaphore accessS;
    private final ISemaphore requestS;

    private final Object counterM = new Object();
    private int readers_no;

    public Resource() {
        this.data = "DATA_NULL";
        this.readers_no = 0;
        this.accessS = new BinarySemaphore();
        this.requestS = new BinarySemaphore();
    }

    public String read() throws InterruptedException {

        requestS.p();
        synchronized (counterM) {

            readers_no++;

            if (readers_no == 1) {
                accessS.p();
            }

        }
        requestS.v();

        Thread.sleep((int) (Math.random() * 100));

        System.out.println();

        try {
            return data;
        } finally {
            synchronized (counterM) {
                readers_no--;
                if (readers_no == 0) {
                    accessS.v();
                }
            }
        }


    }

    public void write(String data) throws InterruptedException {
        requestS.p();
        accessS.p();
        requestS.v();

        Thread.sleep((int)(Math.random()*100));

        this.data = data;

        accessS.v();

    }
}
