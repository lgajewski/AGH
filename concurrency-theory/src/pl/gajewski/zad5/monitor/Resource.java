package pl.gajewski.zad5.monitor;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Resource {

    private String data;

    private final Object writeLock = new Object();
    private final Object readWriteLock = new Object();
    private boolean isReading = false;
    private boolean writeRequest = false;

    public Resource() {
        this.data = "DATA_NULL";
    }

    public String read() throws InterruptedException {
        synchronized (readWriteLock) {
            if(!isReading) {
                while (writeRequest) {
                    readWriteLock.wait();
                }
            }
            isReading = true;
        }

        Thread.sleep((int)(Math.random()*100));

        try {
            return data;
        } finally {
            synchronized (readWriteLock) {
                isReading = false;
                readWriteLock.notifyAll();
            }
        }


    }

    public void write(String data) throws InterruptedException {
        synchronized (readWriteLock) {
            writeRequest = true;
            while (isReading) {
                readWriteLock.wait();
            }
        }

        synchronized (writeLock) {

            Thread.sleep((int)(Math.random()*100));

            writeRequest = false;
            this.data = data;

            synchronized (readWriteLock) {
                readWriteLock.notifyAll();
            }
        }
    }
}
