package org.kylin.protocol.message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jimmey on 15-6-26.
 */
public class Mid {

    private static final AtomicInteger seq = new AtomicInteger();

    public static int next() {
        int n = seq.incrementAndGet();
        if (n == Integer.MAX_VALUE) {
            seq.set(0);
        }
        return n;
    }

}
