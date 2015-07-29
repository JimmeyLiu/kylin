/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) �Ա�(�й�) 2003-2014
 */
package org.kylin.processor.handler.traffic.tps;


import org.kylin.common.log.RpcLogger;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <ol>
 * <li>����rate > 0����λΪ����/��
 * <li>ʱ�䴰timeWindow >= 1����λΪ����
 * <li>��ֵpeak >= rate * timeWindow / 1000.0
 * </ol>
 * <p/>
 * �����������
 */
public class TrafficControl {
    static private final Logger logger = RpcLogger.getLogger();

    private static final int DEFAULT_RATE = 50;
    private static final int DEFAULT_PEAK = 100;
    private static final int DEFAULT_TIME_WINDOW = 1000;

    // ��̬�У�ÿ������ĵ��ô���
    private int rate;
    // ͻ�����÷�ֵ�����ޣ�������Ͱ����
    private int peak;
    // ����Ͱˢ����С�������λ����
    private int timeWindow;

    // ��ǰ������������
    private AtomicInteger tokens;
    // ��һ��ˢ������Ͱ��ʱ��
    private volatile long lastRefreshTime;

    private volatile double leftDouble;

    public TrafficControl(int rate, int peak, int timeWindow) {
        this.rate = rate;
        this.peak = peak;
        this.timeWindow = timeWindow;

        double initialToken = rate * timeWindow / 1000d;
        // �����ʼ��tokenΪ�㲻���� ��Ϊ1��
        this.tokens = initialToken >= 1 ? new AtomicInteger((int) initialToken) : new AtomicInteger(1);
        // ���Ӵ˱���ֵ����Ϊ��doubleתintʱ��Ĳ���ȷ��������ۼ�������ۼƵĽӹ���ǳ���
        this.leftDouble = initialToken - Math.floor(initialToken);
        this.lastRefreshTime = System.currentTimeMillis();
    }

    /**
     * �������ǰ�����ȸ�����������
     */
    public boolean check() {
        long now = System.currentTimeMillis();
        // ���Ը�����������
        if (now > lastRefreshTime + timeWindow) {
            int currentValue = tokens.get();
            double interval = (now - lastRefreshTime) / 1000d;
            double addedDouble = interval * rate;
            int added = (int) addedDouble; // ���ֵΪInteger.MAX_VALUE
            if (added > 0) {
                double addedPlusDouble = leftDouble + (addedDouble - added);
                int addPlus = (int) addedPlusDouble;
                added += addPlus;
                int newValue = currentValue + added;
                newValue = (newValue > currentValue && newValue < peak) ? newValue : peak;
                if (tokens.compareAndSet(currentValue, newValue)) {
                    // ���³ɹ��������µ�ˢ��ʱ��
                    lastRefreshTime = now;
                    leftDouble = addedPlusDouble - addPlus;
                    if (logger.isDebugEnabled()) {
                        logger.debug("[TrafficControl] Updated done: [{}] -> [{}], refresh time: {}.", currentValue, newValue, now);
                    }
                }
            }
        }

        // ���Ի��һ������
        int value = tokens.get();
        boolean flag = false; // �Ƿ��õ�һ������
        while (value > 0 && !flag) {
            flag = tokens.compareAndSet(value, value - 1);
            value = tokens.get();
        }

        if (logger.isDebugEnabled()) {
            if (!flag) {
                logger.debug("TrafficControl: get token failed, tokens[" + tokens.get() + "]");
            }
        }
        return flag;
    }

    @Override
    public String toString() {
        return "TrafficControl [tokens=" + tokens + ", rate=" + rate + ", peak=" + peak + ", timeWindow="
                + timeWindow + "]";
    }

}
