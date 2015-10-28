package pl.gajewski.zad4.b;


/**
 * @author Gajo
 *         29/03/2015
 */



public class Main {

    private static final int MULTIPLE = 10;

    private static final int THREADS_NO = 1;   // per consumer / producer

    private static final int M = 10;

    public static void main(String[] args) throws InterruptedException {

        for(int i=0; i<THREADS_NO; i++) {        Buffer buffer = new Buffer(2*M);

            new Producer(buffer, MULTIPLE, M).start();
            new Consumer(buffer, MULTIPLE, M).start();
        }

    }
}
