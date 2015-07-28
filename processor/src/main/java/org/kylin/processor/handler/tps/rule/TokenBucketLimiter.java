/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) �Ա�(�й�) 2003-2014
 */
package org.kylin.processor.handler.tps.rule;


import org.kylin.common.log.RpcLogger;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * �������̰߳�ȫ������Ͱ��������ʱ�䴰ˢ�����Ϊ���뼶�� ������Ч����������Ҫ���㣺
 * <ol>
 * <li>����rate > 0����λΪ����/��
 * <li>ʱ�䴰timeWindow >= 1����λΪ����
 * <li>��ֵpeak >= rate * timeWindow / 1000.0
 * </ol>
 *
 * @author yijiang
 * @since 1.4.8.3
 */
public class TokenBucketLimiter {
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

    public TokenBucketLimiter() {
        this(DEFAULT_RATE, DEFAULT_PEAK, DEFAULT_TIME_WINDOW);
    }

    public TokenBucketLimiter(int rate, int peak, int timeWindow) {
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
                        logger.debug("[TokenBucketLimiter] Updated done: [{}] -> [{}], refresh time: {}.", currentValue, newValue, now);
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
                logger.debug("TokenBucketLimiter: get token failed, tokens[" + tokens.get() + "]");
            }
        }
        return flag;
    }

    @Override
    public String toString() {
        return "TokenBucketLimiter [tokens=" + tokens + ", rate=" + rate + ", peak=" + peak + ", timeWindow="
                + timeWindow + "]";
    }

    /**
     * ��������Ч����֤�������������ñ�����������������
     * <ol>
     * <li>����rate����ֵpeak����Ϊ����0
     * <li>ʱ�䴰timeWindow��С��1
     * <li>��ֵ��С��������ʱ�䴰�ĳ˻�
     * </ol>
     *
     * @return true/false
     */
    public boolean validate() {
        if (rate <= 0 || peak <= 0 || timeWindow < 1) {
            return false;
        }
        if (peak < (rate * timeWindow / 1000F)) {
            return false;
        }
        return true;
    }

}
