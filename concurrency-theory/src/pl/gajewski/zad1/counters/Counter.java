package pl.gajewski.zad1.counters;

public class Counter implements ICounter {

    private int counter;

    public Counter(int initialCounter) {
        this.counter = initialCounter;
    }

    public void increment() {
        counter++;
    }

    public void decrement() {
        counter--;
    }

    public int getValue() {
        return counter;
    }

}
