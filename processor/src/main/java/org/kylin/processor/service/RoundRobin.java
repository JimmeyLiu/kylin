package org.kylin.processor.service;

import org.kylin.address.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jimmey on 15-6-22.
 */
@SuppressWarnings("unchecked")
public class RoundRobin<E> {

    List<E> list;
    private E[] elements;

    public RoundRobin() {
        this.list = new ArrayList<E>();
        this.elements = (E[]) list.toArray();
    }

    /**
     * @return null if no element
     */
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

    public static void main(String[] args) {
        RoundRobin<Address> roundRobin = new RoundRobin<Address>();
        Address address = Address.parse("127.0.0.1:18000?IDLE_TIMEOUT=60&CONNECT_TIMEOUT=1000&SERIALIZE=msgpack,json");
        roundRobin.add(address);
        roundRobin.add(address);
        System.out.println(roundRobin.getElements().size());
        roundRobin.remove(address);
        System.out.println(roundRobin.getElements().size());


    }

}
