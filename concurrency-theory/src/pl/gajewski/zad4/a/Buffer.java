package pl.gajewski.zad4.a;

import pl.gajewski.zad3.semaphore.BinarySemaphore;
import pl.gajewski.zad3.semaphore.CountingSemaphore;
import pl.gajewski.zad3.semaphore.ISemaphore;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Buffer {

    private Stack<Integer> buff;
    private final Lock l;
    private final int max;

    // occurs when buff is empty and get() method called
    private Condition cEmpty;

    // occurs when buff is full and put() method called
    private Condition cFull;

    public Buffer(int max) {
        this.max = max;
        this.buff = new Stack<Integer>();

        this.l = new ReentrantLock();
        this.cEmpty = l.newCondition();
        this.cFull = l.newCondition();
    }

    public int get() throws InterruptedException {
        l.lock();
        try {
            while(buff.isEmpty()) cEmpty.await();
            int result = buff.pop();
            cFull.signal();
            return result;
        } finally {
            l.unlock();
        }
    }

    public void put(int val) throws InterruptedException {
        l.lock();
        try {
            while(buff.size() >= max) cFull.await();
            buff.push(val);
            cEmpty.signal();
        } finally {
            l.unlock();
        }
    }

}
