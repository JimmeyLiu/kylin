package org.kylin.processor.handler.tps;

import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.processor.handler.RequestHandler;
import org.kylin.processor.handler.tps.rule.TpsRule;
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
public class TpsHandler implements RequestHandler {

    InvocationCollector collector;
    ConcurrentMap<String, TpsRule> tpsRules;
    Logger logger = RpcLogger.getLogger();

    public TpsHandler() {
        collector = new InvocationCollector();
        this.tpsRules = new ConcurrentHashMap<String, TpsRule>();
    }

    @Override
    public void handle(Request request, Response response) {
        collector.accumulate(request);
        TpsRule tpsRule = tpsRules.get(request.getServiceKey());
        if (tpsRule == null) {
            return;
        }
        String appName = RequestCtxUtil.getClientAppName();
        if (tpsRule.hitAppRule(appName)) {
            limitedLog(request, "AppRule");
            response.setStatus(StatusCode.TPS_LIMITED);
            return;
        }

        if (tpsRule.hitMethodRule(request.getMethod())) {
            limitedLog(request, "MethodRule");
            response.setStatus(StatusCode.TPS_LIMITED);
            return;
        }
        if (tpsRule.hitServiceRule()) {
            limitedLog(request, "ServiceRule");
            response.setStatus(StatusCode.TPS_LIMITED);
            return;
        }
    }

    private void limitedLog(Request request, String rule) {
        logger.error("[TPS Limited] appName %s service %s.%s hit %s",
                RequestCtxUtil.getClientAppName(), request.getServiceKey(), request.getMethod(), rule);
    }
}
