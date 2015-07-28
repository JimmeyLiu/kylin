/**
 * High-Speed Service Framework (HSF)
 *
 * www.taobao.com
 *  (C) 淘宝(中国) 2003-2014
 */
package org.kylin.processor.handler.tps;

import org.kylin.protocol.message.Request;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 描述：调用收集器统计结点{@link StatNode}列表
 *
 * @author yijiang
 * @since 1.4.8.3
 */
public class StatList extends CopyOnWriteArrayList<StatNode> {

    private static final long serialVersionUID = 4443515283673903387L;

    /**
     * 添加HSF请求到统计数据
     *
     * @param request HSF请求
     */
    public void append(Request request) {
        for (StatNode node : this) {
            if (node.incrementIfMatches(request)) { // 添加到已有统计结点上
                return;
            }
        }
        // 新增统计结点
        StatNode node = new StatNode(request);
        add(node);
    }
}
