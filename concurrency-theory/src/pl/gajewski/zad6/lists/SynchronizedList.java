package pl.gajewski.zad6.lists;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gajo
 *         04/05/2015
 */

public class SynchronizedList<T> implements ILockList<T> {

    private Node<T> head;
    private Lock l;

    public SynchronizedList() {
        this.head = new Node<T>(null);
        this.l = new ReentrantLock();
    }

    @Override
    public void add(T key) {
        l.lock();
        try {
            Node<T> current = head;
            while (current.hasNext()) {
                current = current.getNext();
            }
            // current is last elem
            current.setNext(new Node<T>(key));
        } finally {
            l.unlock();
        }
    }

    @Override
    public T remove(T key) {
        l.lock();
        try {
            Node<T> current = head;
            Node<T> next;
            while (current.hasNext()) {
                next = current.getNext();
                if (next.getItem() == key) {
                    // remove elem
                    current.setNext(next.getNext());
                    next.setNext(null);
                    return next.getItem();
                }
                current = current.getNext();
            }
            return null;
        } finally {
            l.unlock();
        }
    }

    @Override
    public boolean contains(T key) {
        l.lock();
        try {
            Node<T> current = head;
            Node<T> next;
            while (current.hasNext()) {
                    next = current.getNext();
                    if(next.getItem() == key) return true;
                    current = current.getNext();
            }
            return false;
        } finally {
            l.unlock();
        }
    }

    @Override
    public void print() {
        l.lock();
        try {
            Node<T> current = head;
            System.out.print("SynchronizedList: ");
            while(current.hasNext()) {
                System.out.print("'" + current.getNext().getItem() + "' ");
                current = current.getNext();
            }
        } finally {
            l.unlock();
        }
    }

    class Node<I> {

        private I item;
        private Node<I> next;

        private Node(I key) {
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
    }

}