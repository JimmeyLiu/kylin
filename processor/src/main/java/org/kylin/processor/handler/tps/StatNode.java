/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) �Ա�(�й�) 2003-2014
 */
package org.kylin.processor.handler.tps;


import org.kylin.protocol.message.Request;

/**
 * ������������ͳ����ͳ�ƽ�㣬ͳ���˶�ĳ�����ĵ���ͳ����Ϣ
 *
 * @author yijiang
 * @since 1.4.8.3
 */
public class StatNode {

    /**
     * ����{@link org.kylin.protocol.message.Request}����Ŀ��ǩ��������Ϊ������
     * <p/>
     * ���ɹ��� ��������_��������_��������1����_��������2����...��������n���ͣ�����������ȫ��תΪСд��
     *
     * @param request
     * @return ǩ��
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

    // ����Ŀ��Ωһ��ʶ
    private String key;
    // ��������
    private long amount;

    public StatNode(Request request) {
        this.key = getRequestSignature(request);
        this.amount = 1L;
    }

    /**
     * ��ǰͳ�ƽ���Ƿ�����ĳ������ʹ��keyֵ�ж�
     *
     * @param serviceUniqueName �������Ʊ�ʶ
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
     * �ڽ��ƥ���ڵ�ǰ��HSF���������£����ӵ�����
     *
     * @param request HSF����
     */
    public boolean incrementIfMatches(Request request) {
        if (matches(request)) {
            amount++;
            return true;
        }
        return false;
    }

    /**
     * ����Ƿ�ƥ���ڵ�ǰ��HSF����
     *
     * @param request HSF����
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
