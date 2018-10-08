package pl.gajewski.zad1.threads;

import pl.gajewski.zad1.counters.ICounter;

public class IncThread extends Thread {

    public final int MULTIPLE;

    public final ICounter counter;

    public IncThread(ICounter counter, int MULTIPLE) {
        this.counter = counter;
        this.MULTIPLE = MULTIPLE;
    }

    @Override
    public void run() {

        for(int i=0; i<MULTIPLE; i++) {
            counter.increment();
        }

    }
}
