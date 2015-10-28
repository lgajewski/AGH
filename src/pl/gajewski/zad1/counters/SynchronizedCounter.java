package pl.gajewski.zad1.counters;

public class SynchronizedCounter implements ICounter {

    private int counter;

    public SynchronizedCounter(int initialCounter) {
        this.counter = initialCounter;
    }

    synchronized public void increment() {
        counter++;
    }

    synchronized public void decrement() {
        counter--;
    }

    public int getValue() {
        return counter;
    }


}
