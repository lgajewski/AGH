package pl.gajewski.zad2;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int MULTIPLE = 2000;
    private static final int MAX_BUFFER = 10;

    private static final int THREADS_NO = 100;   // per consumer / producer

    public static void main(String[] args) throws InterruptedException {

        List<Thread> threads = new ArrayList<Thread>();

        Buffer buffer = new Buffer(MAX_BUFFER);
        for(int i=0; i<THREADS_NO; i++) {
            threads.add(new Producer(buffer, MULTIPLE));
            threads.add(new Consumer(buffer, MULTIPLE));
        }

        long start = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long end = System.currentTimeMillis();

        System.out.println("Time: " + (end - start));
    }
}
