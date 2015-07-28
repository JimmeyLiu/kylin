/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) �Ա�(�й�) 2003-2014
 */
package org.kylin.processor.handler.tps;

import org.kylin.protocol.message.Request;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * �����������ռ���ͳ�ƽ��{@link StatNode}�б�
 *
 * @author yijiang
 * @since 1.4.8.3
 */
public class StatList extends CopyOnWriteArrayList<StatNode> {

    private static final long serialVersionUID = 4443515283673903387L;

    /**
     * ���HSF����ͳ������
     *
     * @param request HSF����
     */
    public void append(Request request) {
        for (StatNode node : this) {
            if (node.incrementIfMatches(request)) { // ��ӵ�����ͳ�ƽ����
                return;
            }
        }
        // ����ͳ�ƽ��
        StatNode node = new StatNode(request);
        add(node);
    }
}
