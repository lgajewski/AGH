package pl.gajewski.zad3;

import pl.gajewski.zad3.semaphore.BinarySemaphore;
import pl.gajewski.zad3.semaphore.CountingSemaphore;
import pl.gajewski.zad3.semaphore.ISemaphore;

import java.util.Stack;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Buffer {

    private Stack<Integer> buff;
    private ISemaphore binarySemaphore;
    private ISemaphore countSemaphore;

    public Buffer(int max) {
        this.buff = new Stack<Integer>();
        this.binarySemaphore = new BinarySemaphore();
        this.countSemaphore = new CountingSemaphore(0, max);
    }

    public int get() throws InterruptedException {
        countSemaphore.p();
        binarySemaphore.p();

        int result = buff.pop();

        binarySemaphore.v();
        return result;
    }

    public void put(int val) throws InterruptedException {
        binarySemaphore.p();

        buff.push(val);

        binarySemaphore.v();
        countSemaphore.v();
    }


}
