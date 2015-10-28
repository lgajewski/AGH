package pl.gajewski.zad5.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Resource {

    private String data;

    private final Lock writeLock;
    private final Lock readWriteLock;
    private final Condition readWriteLockCond;
    private boolean isReading = false;
    private boolean writeRequest = false;

    public Resource() {
        this.data = "DATA_NULL";
        writeLock = new ReentrantLock();
        readWriteLock = new ReentrantLock();
        readWriteLockCond = readWriteLock.newCondition();
    }

    public String read() throws InterruptedException {

        readWriteLock.lock();
        try {
            if(!isReading) {
                while (writeRequest) {
                    readWriteLockCond.await();
                }
            }
            isReading = true;
        } finally {
            readWriteLock.unlock();
        }


        try {
            return data;
        } finally {
            readWriteLock.lock();
            try {
                isReading = false;
                readWriteLockCond.signalAll();
            } finally {
                readWriteLock.unlock();
            }
        }


    }

    public void write(String data) throws InterruptedException {
        readWriteLock.lock();
        try {
            writeRequest = true;
            while (isReading) {
                readWriteLockCond.await();
            }
        } finally {
            readWriteLock.unlock();
        }

        writeLock.lock();
        try {
            writeRequest = false;
            this.data = data;

        } finally {
            writeLock.unlock();
            readWriteLock.lock();
            try {
                readWriteLockCond.signalAll();
            } finally {
                readWriteLock.unlock();
            }
        }
    }
}
