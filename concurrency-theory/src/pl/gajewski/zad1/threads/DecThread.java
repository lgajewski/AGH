package pl.gajewski.zad1.threads;

import pl.gajewski.zad1.counters.ICounter;

public class DecThread extends Thread {

    public final int MULTIPLE;

    public final ICounter counter;

    public DecThread(ICounter counter, int MULTIPLE) {
        this.counter = counter;
        this.MULTIPLE = MULTIPLE;
    }

    @Override
    public void run() {

        for(int i=0; i<MULTIPLE; i++) {
            counter.decrement();
        }

    }
}
