/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) 淘宝(中国) 2003-2014
 */
package org.kylin.processor.handler.traffic.tps;


import org.kylin.common.log.RpcLogger;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <ol>
 * <li>速率rate > 0，单位为次数/秒
 * <li>时间窗timeWindow >= 1，单位为毫秒
 * <li>峰值peak >= rate * timeWindow / 1000.0
 * </ol>
 * <p/>
 * 流量控制组件
 */
public class TrafficControl {
    static private final Logger logger = RpcLogger.getLogger();

    private static final int DEFAULT_RATE = 50;
    private static final int DEFAULT_PEAK = 100;
    private static final int DEFAULT_TIME_WINDOW = 1000;

    // 稳态中，每秒允许的调用次数
    private int rate;
    // 突发调用峰值的上限，即令牌桶容量
    private int peak;
    // 令牌桶刷新最小间隔，单位毫秒
    private int timeWindow;

    // 当前可用令牌数量
    private AtomicInteger tokens;
    // 下一次刷新令牌桶的时间
    private volatile long lastRefreshTime;

    private volatile double leftDouble;

    public TrafficControl() {
        this(DEFAULT_RATE, DEFAULT_PEAK, DEFAULT_TIME_WINDOW);
    }

    public TrafficControl(int rate, int peak, int timeWindow) {
        this.rate = rate;
        this.peak = peak;
        this.timeWindow = timeWindow;

        double initialToken = rate * timeWindow / 1000d;
        // 如果初始的token为零不合理， 改为1。
        this.tokens = initialToken >= 1 ? new AtomicInteger((int) initialToken) : new AtomicInteger(1);
        // 增加此保存值，是为了double转int时候的不精确；如果不累及这个误差，累计的接过会非常大。
        this.leftDouble = initialToken - Math.floor(initialToken);
        this.lastRefreshTime = System.currentTimeMillis();
    }

    /**
     * 检查令牌前，首先更新令牌数量
     */
    public boolean check() {
        long now = System.currentTimeMillis();
        // 尝试更新令牌数量
        if (now > lastRefreshTime + timeWindow) {
            int currentValue = tokens.get();
            double interval = (now - lastRefreshTime) / 1000d;
            double addedDouble = interval * rate;
            int added = (int) addedDouble; // 最大值为Integer.MAX_VALUE
            if (added > 0) {
                double addedPlusDouble = leftDouble + (addedDouble - added);
                int addPlus = (int) addedPlusDouble;
                added += addPlus;
                int newValue = currentValue + added;
                newValue = (newValue > currentValue && newValue < peak) ? newValue : peak;
                if (tokens.compareAndSet(currentValue, newValue)) {
                    // 更新成功后，设置新的刷新时间
                    lastRefreshTime = now;
                    leftDouble = addedPlusDouble - addPlus;
                    if (logger.isDebugEnabled()) {
                        logger.debug("[TrafficControl] Updated done: [{}] -> [{}], refresh time: {}.", currentValue, newValue, now);
                    }
                }
            }
        }

        // 尝试获得一个令牌
        int value = tokens.get();
        boolean flag = false; // 是否获得到一个令牌
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

    /**
     * 限流器有效性验证。限流器的配置必须满足以下条件：
     * <ol>
     * <li>速率rate、峰值peak配置为大于0
     * <li>时间窗timeWindow不小于1
     * <li>峰值不小于速率与时间窗的乘积
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
