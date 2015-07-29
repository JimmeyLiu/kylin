/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) ÌÔ±¦(ÖÐ¹ú) 2003-2014
 */
package org.kylin.processor.handler.traffic;


import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Request;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficStat {

    static private final Logger logger = RpcLogger.getLogger();
    private static final long DEFAULT_TIME_WINDOW = 1000 * 30;
    private long timeWindow;
    private volatile long lastRefreshTime;
    private ConcurrentMap<String, AtomicInteger> collector = new ConcurrentHashMap<String, AtomicInteger>();

    public TrafficStat() {
        timeWindow = DEFAULT_TIME_WINDOW;
        lastRefreshTime = System.currentTimeMillis();
    }

    /**
     * @param request
     */
    public void accumulate(Request request) {
        long now = System.currentTimeMillis();
        if (now > lastRefreshTime + timeWindow) {
            lastRefreshTime = now;
            doLog();
            collector.clear();
        }

        String key = getRequestSignature(request);
        AtomicInteger c = collector.get(key);
        if (c == null) {
            c = new AtomicInteger();
            collector.put(key, c);
        }
        c.incrementAndGet();
    }

    public static String getRequestSignature(Request request) {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getServiceKey()).append(" ").append(request.getMethod());
        for (String s : request.getArgTypes()) {
            if (s != null) {
                sb.append("_").append(s.replace("/", "."));
            } else {
                sb.append("_null");
            }
        }
        sb.append(" ").append(RequestCtxUtil.getClientAppName());
        return sb.toString();
    }

    private void doLog() {
        for (Map.Entry<String, AtomicInteger> entry : collector.entrySet()) {
            logger.info("{} {}", entry.getKey(), entry.getValue().get());
        }
    }
}
