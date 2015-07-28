/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) 淘宝(中国) 2003-2014
 */
package org.kylin.processor.handler.tps;


import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Request;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * from hsf
 */
public class InvocationCollector {

    static private final Logger LOGGER = RpcLogger.getLogger();
    private static final long DEFAULT_TIMEWINDOW = 1000 * 10;

    // 统计器刷新时间间隔
    private long timeWindow;
    // 最后刷新时间
    private volatile long lastRefreshTime;
    // 统计器
    private ConcurrentMap<String, StatList> collector = new ConcurrentHashMap<String, StatList>();

    public InvocationCollector() {
        timeWindow = DEFAULT_TIMEWINDOW;
        lastRefreshTime = System.currentTimeMillis();
    }

    /**
     * 累加调用数量<br />
     * 在执行累加前，判断否已经到刷新时间，如果是，首先记录日志并清空统计器。
     *
     * @param request
     */
    public void accumulate(Request request) {
        long now = System.currentTimeMillis();
        // 清空统计器
        if (now > lastRefreshTime + timeWindow) {
            lastRefreshTime = now;
            doLog();
            collector.clear();
        }

        String appName = RequestCtxUtil.getClientAppName();

        StatList list = collector.get(appName);
        if (list == null) {
            list = new StatList();
            collector.put(appName, list);
        }
        list.append(request);
    }

    /**
     * 返回某个服务消费者的总调用次数
     *
     * @param appName
     * @return 调用总次数
     */
    public long getConsumerAmount(String appName) {
        StatList list = collector.get(appName);
        long amount = 0;
        if (list != null && !list.isEmpty()) {
            for (StatNode node : list) {
                amount += node.getAmount();
            }
        }
        return amount;
    }

    /**
     * 返回统计的方法被调用总次数
     *
     * @param request HSF请求
     * @return 被调用总次数
     */
    public long getMethodAmount(Request request) {
        long amount = 0;
        for (StatList list : collector.values()) {
            for (StatNode node : list) {
                if (node.matches(request)) {
                    amount += node.getAmount();
                }
            }
        }
        return amount;
    }

    /**
     * 返回统计的方法被调用总次数
     *
     * @param request HSF请求
     * @return 被调用总次数
     */
    public long getServiceAmount(Request request) {
        long amount = 0;
        for (StatList list : collector.values()) {
            for (StatNode node : list) {
                if (node.belongsTo(request.getServiceKey())) {
                    amount += node.getAmount();
                }
            }
        }
        return amount;
    }

    public void setTimeWindow(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Override
    public String toString() {
        return "InvocationCollector [collector=" + collector + "]";
    }

    /**
     * 将当前统计器中统计的调用量数据写入日志
     */
    private void doLog() {
        StringBuffer sb = new StringBuffer();
        sb.append("Kylin RPC Invoke Stat\n");
        for (String appName : collector.keySet()) {
            sb.append("|--App [").append(appName).append("]\n");
            for (StatNode node : collector.get(appName)) {
                sb.append("  |--").append(node.getKey()).append("   ").append(node.getAmount()).append("\n");
            }
        }
        LOGGER.info(sb.toString());
        System.out.println(sb.toString());
    }
}
