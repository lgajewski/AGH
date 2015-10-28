package pl.gajewski.zad6.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.gajewski.zad6.lists.ILockList;
import pl.gajewski.zad6.lists.SynchronizedList;

import static org.junit.Assert.*;

/**
 * @author Gajo
 *         04/05/2015
 */

public class SynchronizedListTest {

    ILockList<Object> list;

    @Before
    public void setUp() throws Exception {
        list = new SynchronizedList<Object>();
    }

    @Test
    public void testAdd() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        list.add(obj1);
        assertTrue(list.contains(obj1));

        list.add(obj2);
        assertTrue(list.contains(obj2));
    }

    @Test
    public void testRemove() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        list.add(obj1);
        list.add(obj2);
        assertTrue(list.contains(obj1));
        assertTrue(list.contains(obj2));

        list.remove(obj1);
        assertFalse(list.contains(obj1));

        list.remove(obj2);
        assertFalse(list.contains(obj2));
    }

    @Test
    public void testContains() throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        list.add(obj1);
        list.add(obj2);

        assertTrue(list.contains(obj1));
        assertTrue(list.contains(obj2));
    }
}