package org.kylin.protocol.message;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jimmey on 15-6-26.
 */
public class Mid {

    private static final AtomicLong seq = new AtomicLong();

    public static long next() {
        long n = seq.incrementAndGet();
        if (n == Long.MAX_VALUE) {
            seq.set(0);
        }
        return n;
    }

}
