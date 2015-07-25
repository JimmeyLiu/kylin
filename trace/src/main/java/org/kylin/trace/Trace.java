package org.kylin.trace;

/**
 * Created by jimmey on 15-7-1.
 */
public class Trace {

    private static ThreadLocal<TraceContext> threadLocal = new ThreadLocal<TraceContext>();

    public static void start() {
        TraceContext context = new TraceContext();
        threadLocal.set(context);
    }

    public static void startRpc() {

    }

    public static void end(ResultCode resultCode) {

    }

    public static class TraceContext {
        String traceId;
        String step;
    }

}
