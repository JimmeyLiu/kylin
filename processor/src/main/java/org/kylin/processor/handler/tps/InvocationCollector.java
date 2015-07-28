/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) �Ա�(�й�) 2003-2014
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

    // ͳ����ˢ��ʱ����
    private long timeWindow;
    // ���ˢ��ʱ��
    private volatile long lastRefreshTime;
    // ͳ����
    private ConcurrentMap<String, StatList> collector = new ConcurrentHashMap<String, StatList>();

    public InvocationCollector() {
        timeWindow = DEFAULT_TIMEWINDOW;
        lastRefreshTime = System.currentTimeMillis();
    }

    /**
     * �ۼӵ�������<br />
     * ��ִ���ۼ�ǰ���жϷ��Ѿ���ˢ��ʱ�䣬����ǣ����ȼ�¼��־�����ͳ������
     *
     * @param request
     */
    public void accumulate(Request request) {
        long now = System.currentTimeMillis();
        // ���ͳ����
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
     * ����ĳ�����������ߵ��ܵ��ô���
     *
     * @param appName
     * @return �����ܴ���
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
     * ����ͳ�Ƶķ����������ܴ���
     *
     * @param request HSF����
     * @return �������ܴ���
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
     * ����ͳ�Ƶķ����������ܴ���
     *
     * @param request HSF����
     * @return �������ܴ���
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
     * ����ǰͳ������ͳ�Ƶĵ���������д����־
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
