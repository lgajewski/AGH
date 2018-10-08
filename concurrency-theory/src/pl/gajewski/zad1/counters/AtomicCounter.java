package pl.gajewski.zad1.counters;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gajo
 *         17/03/2015
 */

public class AtomicCounter implements ICounter {

    private AtomicInteger counter;

    public AtomicCounter(int initial_value) {
        counter = new AtomicInteger(initial_value);
    }

    @Override
    public void increment() {
        counter.getAndIncrement();
    }

    @Override
    public void decrement() {
        counter.getAndDecrement();
    }

    @Override
    public int getValue() {
        return counter.get();
    }
}
