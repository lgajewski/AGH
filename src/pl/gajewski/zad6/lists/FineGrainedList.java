package pl.gajewski.zad6.lists;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gajo
 *         04/05/2015
 */

public class FineGrainedList<T> implements ILockList<T> {

    private final Node<T> head;

    public FineGrainedList() {
        this.head = new Node<T>(null);
    }

    @Override
    public void add(T key) {
        head.lock();
        Node<T> next = head;
        Node<T> current = head;
        try {
            while (current.hasNext()) {
                try {
                    next = current.getNext();
                    next.lock();
                } finally {
                    current.unlock();
                    current = next;
                }
            }
            // next is last element of the list
            next.setNext(new Node<T>(key));
        } finally {
            next.unlock();
        }
    }

    @Override
    public T remove(T key) {
        head.lock();
        Node<T> next = head;
        Node<T> current = head;
        try {
            while (current.hasNext()) {
                try {
                    next = current.getNext();
                    next.lock();
                    if (next.getItem().equals(key)) {
                        // remove elem
                        current.setNext(next.getNext());
                        next.setNext(null);
                        return next.getItem();
                    }
                } finally {
                    current.unlock();
                    current = next;
                }
            }
            return null;
        } finally {
            next.unlock();
        }
    }

    @Override
    public boolean contains(T key) {
        head.lock();
        Node<T> current = head;
        Node<T> next = head;
        try {
            while (current.hasNext()) {
                try {
                    next = current.getNext();
                    next.lock();
                    if(next.getItem() == key) return true;
                } finally {
                    current.unlock();
                    current = next;
                }
            }
            return false;
        } finally {
            next.unlock();
        }
    }

    public void print() {
        head.lock();
        Node<T> next = head;
        Node<T> current = head;
        try {
            System.out.print("FineGrainedList: ");
            while (current.hasNext()) {
                try {
                    next = current.getNext();
                    next.lock();
                    System.out.print("'" + next.getItem() + "' ");
                } finally {
                    current.unlock();
                    current = next;
                }
            }
        } finally {
            current.unlock();
        }
    }


    class Node<I> {

        private I item;
        private Node<I> next;
        private final Lock l;

        private Node(I key) {
            this.l = new ReentrantLock();
            this.item = key;
        }

        private Node<I> getNext() {
            return next;
        }

        private void setNext(Node<I> next) {
            this.next = next;
        }

        private boolean hasNext() {
            return next != null;
        }

        private I getItem() {
            return item;
        }

        private void lock() {
            l.lock();
        }

        private void unlock() {
            l.unlock();
        }
    }

}
