package pl.gajewski.zad6.comparison;

import pl.gajewski.zad6.lists.ILockList;
import pl.gajewski.zad6.lists.SynchronizedList;

import java.util.*;

/**
 * @author Gajo
 *         04/05/2015
 */

public class CompareThread extends Thread {

    private ILockList<Object> list;
    private int multiple;
    private Random rand;
    private Map<Operation, OpValue> operations;

    public CompareThread(ILockList<Object> list, int multiple) {
        this.list = list;
        this.rand = new Random();
        this.multiple = multiple;
        this.operations = new HashMap<Operation, OpValue>();
    }

    public OpValue getOpValue(Operation key) {
        return operations.get(key);
    }

    @Override
    public void run() {
        int number, method_no;
        long start;
        for(int i=0; i<multiple; i++) {
            number = rand.nextInt(Compare.MAX_VALUE);
            method_no = i % 3;

            start = System.currentTimeMillis();

            switch (method_no) {
                case 0:
                    list.add(number);
                    addTime(Operation.ADD, System.currentTimeMillis() - start);
                    break;
                case 1:
                    list.remove(number);
                    addTime(Operation.REMOVE, System.currentTimeMillis() - start);
                    break;
                case 2:
                    list.contains(number);
                    addTime(Operation.CONTAINS, System.currentTimeMillis() - start);
                    break;
                default:
                    throw new IllegalArgumentException("Method not implemented");
            }

        }
    }

    private void addTime(Operation key, long l) {
        OpValue opValue = operations.containsKey(key) ? operations.get(key) : new OpValue();
        opValue.addTime(l);
        operations.put(key, opValue);
    }

}

