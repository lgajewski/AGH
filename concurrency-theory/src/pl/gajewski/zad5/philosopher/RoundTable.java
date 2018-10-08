package pl.gajewski.zad5.philosopher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gajo
 *         20/04/2015
 */

public class RoundTable {

    private static final int N = 2;       // number of philosophers, equal to forkNo

    public static final int MULTIPLE = 3;

    /**
     * RANDOMIZE
     *
     * eatingTime = eatingTime + ROUND * eatingTime
     * sleepingTime = sleepingTime + Round * sleepingTime
     *
     * ROUND = 0 for clear effect
     *
     */

    private static final double ROUND = 0;
    private static final int eatingTime = 0;
    private static final int sleepingTime = 0;

    public static void main(String[] args) {

        List<Thread> pList = new ArrayList<Thread>();

        Fork firstFork = new Fork(0);
        Fork leftFork = firstFork;
        Fork rightFork;

        for(int id=1; id<N; id++) {
            rightFork = new Fork(id);
            Thread philosopher = new Philosopher(id, leftFork, rightFork,
                    getValue(eatingTime), getValue(sleepingTime));
            // add to list
            pList.add(philosopher);

            leftFork = rightFork;
        }

        // add last philosopher
        pList.add(new Philosopher(N, leftFork, firstFork, getValue(eatingTime), getValue(sleepingTime)));

        for (Thread thread : pList) {
            thread.start();
        }

    }

    private static int getValue(int val) {
        double delta = val * (Math.random() * ROUND);
        return (int)(val + delta);
    }

}


