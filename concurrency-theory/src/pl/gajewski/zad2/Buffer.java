package pl.gajewski.zad2;

import java.util.Stack;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Buffer {

    private Stack<Integer> buff;
    private int max;

    public Buffer(int max) {
        buff = new Stack<Integer>();
        this.max = max;
    }

    public synchronized int get() throws InterruptedException {
        while(buff.size() == 0) {
            wait();
        }
        int result = buff.pop();
        notifyAll();
        return result;
    }

    public synchronized void put(int n) throws InterruptedException {
        while(buff.size() > max) {
            wait();
        }
        buff.push(n);
        notifyAll();
    }


}
