package pl.gajewski.zad6.comparison;

import pl.gajewski.zad6.lists.FineGrainedList;
import pl.gajewski.zad6.lists.ILockList;
import pl.gajewski.zad6.lists.SynchronizedList;

import java.util.*;

/**
 * @author Gajo
 *         04/05/2015
 */

public class Compare {

    public static final int MAX_VALUE = 1000;

//    private static ILockList<Object> list = new SynchronizedList<Object>();
    private static ILockList<Object> list = new FineGrainedList<Object>();

    public static void main(String[] args) throws InterruptedException {

        final int N = 10;   // list initial size
        final int M = 99;  // operations number
        final int THREAD_NO = 800;    // thread number

        fillInt(list, N);

        List<CompareThread> threads = new ArrayList<CompareThread>();

        for(int i=0; i<THREAD_NO; i++) {
            threads.add(new CompareThread(list, M));
        }

        // start threads
        for (CompareThread thread : threads) {
            thread.start();
        }

        // join
        for (CompareThread thread : threads) {
            thread.join();
        }

        System.out.println();

        // print operations
        Map<Operation, OpValue> opValues = new TreeMap<Operation, OpValue>();

        for (CompareThread thread : threads) {
            for (Operation operation : Operation.values()) {
                OpValue opValue = thread.getOpValue(operation);
                long time = opValue != null ? opValue.getTime() : 0l;
                int count = opValue != null ? opValue.getCount() : 0;
                addTime(opValues, operation, count, time);
            }
        }

        for (Operation operation : opValues.keySet()) {
            OpValue opValue = opValues.get(operation);
//            System.out.printf("%s; %d; %d\n", operation, THREAD_NO, opValue.getTime()/(THREAD_NO));
            System.out.printf("%-12s N: %-6d T_THREAD: %-7d T_OP: %d\n", operation, THREAD_NO,
                    opValue.getTime()/(THREAD_NO), opValue.getTime()/(THREAD_NO*M));
        }

    }

    private static void addTime(Map<Operation, OpValue> operations, Operation key, int count, long l) {
        OpValue opValue = operations.containsKey(key) ? operations.get(key) : new OpValue();
        opValue.addTime(l, count);
        operations.put(key, opValue);
    }

    private static void fillInt(ILockList<Object> list, int n) {
        Random rand = new Random();
        for(int i=0; i<n; i++) {
            list.add(rand.nextInt(MAX_VALUE));
        }
    }

}
