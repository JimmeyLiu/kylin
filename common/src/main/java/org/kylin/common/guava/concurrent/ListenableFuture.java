package org.kylin.common.guava.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Created by jimmey on 15-6-25.
 */
public interface ListenableFuture<V> extends Future<V> {
    void addListener(Runnable listener, Executor executor);
}
