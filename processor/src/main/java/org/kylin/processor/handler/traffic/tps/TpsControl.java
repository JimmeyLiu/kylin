package org.kylin.processor.handler.traffic.tps;

import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Request;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmey on 15-7-28.
 */
public class TpsControl {

    static Map<String, TrafficControl> appRule = new HashMap<String, TrafficControl>();
    static Map<String, TrafficControl> serviceRule = new HashMap<String, TrafficControl>();
    static Map<String, TrafficControl> serviceMethodRule = new HashMap<String, TrafficControl>();

    static {
        //get TPS control rule from config service
//        serviceRule.put("org.kylin.test.service.TestService:1.0.0", new TrafficControl(1, 2, 1000));
    }

    public static boolean pass(Request request) {
        String appName = RequestCtxUtil.getClientAppName();
        TrafficControl trafficControl = appRule.get(appName);
        if (trafficControl != null && !trafficControl.check()) {
            limitedLog(request, "AppRule");
            return false;
        }

        trafficControl = serviceRule.get(request.getServiceKey());
        if (trafficControl != null && !trafficControl.check()) {
            limitedLog(request, "ServiceRule");
            return false;
        }

        trafficControl = serviceMethodRule.get(request.getServiceKey() + "." + request.getMethod());
        if (trafficControl != null && !trafficControl.check()) {
            limitedLog(request, "ServiceMethodRule");
            return false;
        }
        return true;
    }

    static Logger logger = RpcLogger.getLogger();

    private static void limitedLog(Request request, String rule) {
        logger.error("[TPS Limited] {} invoke {}.{} hit {}",
                RequestCtxUtil.getClientAppName(), request.getServiceKey(), request.getMethod(), rule);
    }
}
