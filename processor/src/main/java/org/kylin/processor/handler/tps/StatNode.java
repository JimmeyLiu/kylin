/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) 淘宝(中国) 2003-2014
 */
package org.kylin.processor.handler.tps;


import org.kylin.protocol.message.Request;

/**
 * 描述：调用量统计器统计结点，统计了对某方法的调用统计信息
 *
 * @author yijiang
 * @since 1.4.8.3
 */
public class StatNode {

    /**
     * 生成{@link org.kylin.protocol.message.Request}请求目标签名，粒度为方法。
     * <p/>
     * 生成规则： 服务名称_方法名称_方法参数1类型_方法参数2类型...方法参数n类型，（参数类型全部转为小写）
     *
     * @param request
     * @return 签名
     */
    public static String getRequestSignature(Request request) {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getServiceKey()).append("_").append(request.getMethod());
        for (String s : request.getArgTypes()) {
            if (s != null) {
                sb.append("_").append(s.toLowerCase());
            } else {
                sb.append("_null");
            }
        }
        return sb.toString();
    }

    // 调用目标惟一标识
    private String key;
    // 调用总量
    private long amount;

    public StatNode(Request request) {
        this.key = getRequestSignature(request);
        this.amount = 1L;
    }

    /**
     * 当前统计结点是否属于某个服务，使用key值判断
     *
     * @param serviceUniqueName 服务名称标识
     * @return true/false
     */
    public boolean belongsTo(String serviceUniqueName) {
        if (serviceUniqueName == null || serviceUniqueName.length() == 0) {
            return false;
        }
        return key.startsWith(serviceUniqueName);
    }

    public long getAmount() {
        return amount;
    }

    public String getKey() {
        return key;
    }

    /**
     * 在结点匹配于当前的HSF请求的情况下，增加调用量
     *
     * @param request HSF请求
     */
    public boolean incrementIfMatches(Request request) {
        if (matches(request)) {
            amount++;
            return true;
        }
        return false;
    }

    /**
     * 结点是否匹配于当前的HSF请求
     *
     * @param request HSF请求
     * @return true/false
     */
    public boolean matches(Request request) {
        return key.equals(getRequestSignature(request));
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
