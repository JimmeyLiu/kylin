package org.kylin.bootstrap.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jimmey on 15-6-22.
 */
@SuppressWarnings("unchecked")
public class RoundRobin<E> implements Iterator<E> {

    List<E> list;
    private E[] elements;

    public RoundRobin() {
        this.list = new ArrayList<E>();
        this.elements = (E[]) list.toArray();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public E next() {
        if (elements.length == 0) {
            return null;
        }
        long c = index.incrementAndGet();
        if (c > Integer.MAX_VALUE) {
            index.set(0);
        }
        final int i = (int) (c % elements.length);
        return elements[i];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Elements " + list;
    }

    public List<E> getElements() {
        return new ArrayList<E>(list);
    }

    public void add(E e) {
        list.add(e);
        this.elements = (E[]) list.toArray();
    }

    public void remove(E e) {
        boolean r = list.remove(e);
        if (r) {
            this.elements = (E[]) list.toArray();
        }
    }

    private final AtomicLong index = new AtomicLong();

}
