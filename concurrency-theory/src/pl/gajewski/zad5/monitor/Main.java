package pl.gajewski.zad5.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Main {

    private final static int READERS_NO = 10;
    private final static int WRITERS_NO = 10;
    private final static int TIMES = 3;

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        Resource res = new Resource();

        for(int i=1; i<=WRITERS_NO; i++) {
            Thread writer = new Writer(res, i, TIMES);
            threads.add(writer);
        }

        for(int i=1; i<=READERS_NO; i++) {
            Thread reader = new Reader(res, i, TIMES);
            threads.add(reader);
        }

        // randomize
        Collections.shuffle(threads, new Random(System.nanoTime()));

        for (Thread thread : threads) {
            thread.start();
        }

    }

}
