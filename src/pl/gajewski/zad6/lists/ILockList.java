package pl.gajewski.zad6.lists;

/**
 * @author Gajo
 *         04/05/2015
 */

public interface ILockList<T> {

    void add(T key);
    T remove(T key);
    boolean contains(T key);

    void print();

}
