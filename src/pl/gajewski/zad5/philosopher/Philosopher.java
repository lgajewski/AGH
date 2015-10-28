package pl.gajewski.zad5.philosopher;

import java.util.Calendar;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Philosopher extends Thread {

    private int id;
    private final Fork leftFork;
    private final Fork rightFork;

    private int eatingTime;
    private int sleepingTime;

    public Philosopher(int id, Fork leftFork, Fork rightFork, int eatingTime, int sleepingTime) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.eatingTime = eatingTime;
        this.sleepingTime = sleepingTime;
    }

    @Override
    public void run() {

        for(int i=0; i<RoundTable.MULTIPLE; i++) {

            try {
                // sleep some time
                Thread.sleep(sleepingTime);

                boolean readyToHold = false;

                while (!readyToHold) {
                    // try to request left fork
                    synchronized (leftFork) {
                        while (leftFork.isRequested()) {
                            leftFork.wait();
                        }
                        leftFork.request(this);
                    }

                    // try to request right fork
                    synchronized (rightFork) {
                        if (!rightFork.isRequested()) {
                            rightFork.request(this);
                            readyToHold = true;
                        }
                    }

                    if(!readyToHold) {
                        System.out.println(1);
                        synchronized (leftFork) {
                            leftFork.deleteReq();
                            leftFork.notify();
                        }
                    }

                }

                // hold forks
                synchronized (leftFork) {
                    leftFork.hold(this);
                }

                synchronized (rightFork) {
                    rightFork.hold(this);
                }


                int sec = Calendar.getInstance().get(Calendar.SECOND);
                System.out.println("[" + sec + "s] Philosopher #" + id + " eating with: " + leftFork + " -  " + rightFork);

                Thread.sleep(eatingTime);

                // release forks
                synchronized (rightFork) {
                    rightFork.release();
                    rightFork.notify();
                }
                synchronized (leftFork) {
                    leftFork.release();
                    leftFork.notify();
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public String toString() {
        return "Philosopher #" + id + " forks: " + leftFork + " - " + rightFork;
    }
}
