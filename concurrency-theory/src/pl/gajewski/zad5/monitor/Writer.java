package pl.gajewski.zad5.monitor;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Writer extends Thread {

    private int id;
    private int n;
    private Resource res;

    public Writer(Resource res, int id, int n) {
        this.res = res;
        this.id = id;
        this.n = n;
    }

    @Override
    public void run() {
        for(int i=0; i<n; i++) {
            try {
                String data = "<"+id+", " + i + ">";
                res.write(data);
                System.out.println("Writer[" + id + "] wrote: " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
