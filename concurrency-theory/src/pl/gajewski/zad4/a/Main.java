package pl.gajewski.zad4.a;


/**
 * @author Gajo
 *         29/03/2015
 */


public class Main {

    private static final int MULTIPLE = 100;
    private static final int MAX_BUFFER = 10;

    private static final int THREADS_NO = 20;   // per consumer / producer

    public static void main(String[] args) throws InterruptedException {

        Buffer buffer = new Buffer(MAX_BUFFER);
        for(int i=0; i<THREADS_NO; i++) {
            new Producer(buffer, MULTIPLE).start();
            new Consumer(buffer, MULTIPLE).start();
        }

    }
}
