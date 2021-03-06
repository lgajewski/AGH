package pl.gajewski.zad5.semaphore;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Reader extends Thread {

    private int id;
    private int n;
    private Resource res;

    public Reader(Resource res, int id, int n) {
        this.n = n;
        this.id = id;
        this.res = res;
    }

    @Override
    public void run() {
        for(int i=0; i<n; i++) {
            try {
                String data = res.read();
                System.out.println("Reader[" + id + "] read " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
