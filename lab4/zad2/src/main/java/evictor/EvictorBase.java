// **********************************************************************
//
// Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package evictor;

//
// Stripped down LinkedList implementation for use in the Evictor. The
// API is similar to java.util.LinkedList.
//
// Major differences:
//  * listIterator() is not implemented.
//  * Operation riterator() returns a reverse iterator.
//  * This implementation also has the property that an Iterator can be
//    retained over structural changes to the list itself (similar to an
//    STL list).
//

public abstract class EvictorBase implements Ice.ServantLocator {
    private java.util.Map<Ice.Identity, EvictorEntry> _map =
            new java.util.HashMap<Ice.Identity, EvictorEntry>();
    private LinkedList<Ice.Identity> _queue =
            new LinkedList<Ice.Identity>();
    private int _size;

    public EvictorBase() {
        _size = 1000;
    }

    public EvictorBase(int size) {
        _size = size < 0 ? 1000 : size;
    }

    public abstract Ice.Object add(Ice.Current c, Ice.LocalObjectHolder cookie);

    public abstract void evict(Ice.Object servant, Object cookie);

    synchronized public final Ice.Object locate(Ice.Current c, Ice.LocalObjectHolder cookie) {
        //
        // Check if we have a servant in the map already.
        //
        EvictorEntry entry = _map.get(c.id);
        if (entry != null) {
            //
            // Got an entry already, dequeue the entry from its current position.
            //
            entry.queuePos.remove();
        } else {
            //
            // We do not have entry. Ask the derived class to
            // instantiate a servant and add a new entry to the map.
            //
            entry = new EvictorEntry();
            Ice.LocalObjectHolder cookieHolder = new Ice.LocalObjectHolder();
            entry.servant = add(c, cookieHolder); // Down-call
            if (entry.servant == null) {
                return null;
            }
            entry.userCookie = cookieHolder.value;
            entry.useCount = 0;
            _map.put(c.id, entry);
        }

        //
        // Increment the use count of the servant and enqueue
        // the entry at the front, so we get LRU order.
        //
        ++(entry.useCount);
        _queue.addFirst(c.id);
        entry.queuePos = _queue.iterator();
        entry.queuePos.next(); // Position the iterator on the element.

        cookie.value = entry;

        return entry.servant;
    }

    synchronized public final void finished(Ice.Current c, Ice.Object o, Object cookie) {
        EvictorEntry entry = (EvictorEntry) cookie;

        //
        // Decrement use count and check if
        // there is something to evict.
        //
        --(entry.useCount);
        evictServants();
    }

    synchronized public final void deactivate(String category) {
        _size = 0;
        evictServants();
    }

    private void evictServants() {
        //
        // If the evictor queue has grown larger than the limit,
        // look at the excess elements to see whether any of them
        // can be evicted.
        //
        java.util.Iterator<Ice.Identity> p = _queue.riterator();
        int excessEntries = _map.size() - _size;
        for (int i = 0; i < excessEntries; ++i) {
            Ice.Identity id = p.next();
            EvictorEntry e = _map.get(id);
            if (e.useCount == 0) {
                evict(e.servant, e.userCookie); // Down-call
                e.queuePos.remove();
                _map.remove(id);
            }
        }
    }

    private class EvictorEntry {
        Ice.Object servant;
        Object userCookie;
        java.util.Iterator<Ice.Identity> queuePos;
        int useCount;
    }
}
