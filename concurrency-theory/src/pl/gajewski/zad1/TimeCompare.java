package pl.gajewski.zad1;

import pl.gajewski.zad1.counters.AtomicCounter;
import pl.gajewski.zad1.counters.ICounter;
import pl.gajewski.zad1.counters.SynchronizedCounter;
import pl.gajewski.zad1.threads.DecThread;
import pl.gajewski.zad1.threads.IncThread;

public class TimeCompare {

    private final int MULTIPLE;
    private final int INITIAL_VALUE;

    public TimeCompare(int MULTIPLE, int INITIAL_VALUE) {
        this.MULTIPLE = MULTIPLE;
        this.INITIAL_VALUE = INITIAL_VALUE;
    }

    public void compare() throws InterruptedException {
        ICounter atomicCounter = new AtomicCounter(INITIAL_VALUE);
        ICounter synchronizedCounter = new SynchronizedCounter(INITIAL_VALUE);

        long unsafeTime = getCounterTime(atomicCounter);
        long safeTime = getCounterTime(synchronizedCounter);

        System.out.println("AtomicCounter: " + unsafeTime + " ms, value: " + atomicCounter.getValue());
        System.out.println("SynchronizedCounter: " + safeTime + " ms, value: " + synchronizedCounter.getValue());

    }

    public long getCounterTime(ICounter counter) throws InterruptedException {
        IncThread incThread = new IncThread(counter, MULTIPLE);
        DecThread decThread = new DecThread(counter, MULTIPLE);

        long start = System.currentTimeMillis();

        incThread.start();
        decThread.start();

        // wait until threads terminate

        incThread.join();
        decThread.join();

        long end = System.currentTimeMillis();

        return (end - start);

    }

}

