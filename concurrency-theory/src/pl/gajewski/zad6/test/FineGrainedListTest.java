package pl.gajewski.zad6.test;

import org.junit.Before;
import org.junit.Test;
import pl.gajewski.zad6.lists.FineGrainedList;
import pl.gajewski.zad6.lists.ILockList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Gajo
 *         04/05/2015
 */

public class FineGrainedListTest {

    ILockList<Object> list;

    @Before
    public void setUp() throws Exception {
        list = new FineGrainedList<Object>();
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