package pl.gajewski.zad4.b;

import pl.gajewski.zad3.semaphore.BinarySemaphore;
import pl.gajewski.zad3.semaphore.CountingSemaphore;
import pl.gajewski.zad3.semaphore.ISemaphore;

import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Buffer {

    private Stack<Integer> buff;
    private ReentrantLock l;
    private Condition cEmpty;
    private Condition cFull;
    private final int max;

    public Buffer(int max) {
        this.max = max;
        this.buff = new Stack<Integer>();
        this.l = new ReentrantLock(true);
        this.cEmpty = l.newCondition();
        this.cFull = l.newCondition();
    }

    public int[] get(int times) throws InterruptedException {
        l.lock();
        try {
            while(buff.size() < times) cEmpty.await();
            int result[] = new int[times];
            for(int i=0; i<times; i++) {
                result[i] = buff.pop();
            }
            cFull.signalAll();

            return result;
        } finally {
            l.unlock();
        }
    }

    public void put(int val, int times) throws InterruptedException {
        l.lock();
        try {
            while((buff.size() + times - 1) >= max) cFull.await();
            for(int i=0; i<times; i++) {
                buff.push(val);
            }
            cEmpty.signalAll();
        } finally {
            l.unlock();
        }
    }

}
