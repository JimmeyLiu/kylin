package org.kylin.processor.handler.traffic;

import org.kylin.common.command.Command;
import org.kylin.common.log.RpcLogger;
import org.kylin.processor.handler.RequestHandler;
import org.kylin.processor.handler.traffic.tps.TpsControl;
import org.kylin.processor.handler.traffic.whitelist.WhiteListControl;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by jimmey on 15-7-28.
 * 1. 做TPS统计
 * 2. 做TPS限流规则
 */
public class TrafficHandler implements RequestHandler, Command {

    TrafficStat stat;
    ConcurrentMap<String, TpsControl> tpsRules;
    Logger logger = RpcLogger.getLogger();

    public TrafficHandler() {
        stat = new TrafficStat();
        this.tpsRules = new ConcurrentHashMap<String, TpsControl>();
    }

    @Override
    public String pattern() {
        return "traffic\\s*";
    }

    @Override
    public String handle(String msg) {
        return null;
    }

    @Override
    public void handle(Request request, Response response) {
        if (!TpsControl.pass(request)) {
            response.setStatus(StatusCode.TPS_LIMITED_DENY);
            return;
        }
        if (!WhiteListControl.pass(request)) {
            response.setStatus(StatusCode.WHITE_LIST_DENY);
            return;
        }
        stat.accumulate(request);
    }

}
