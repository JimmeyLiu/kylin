package org.kylin.processor.handler.traffic.whitelist;

import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Request;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmey on 15-7-29.
 */
public class WhiteListControl {

    private static List<String> whiteListApps = new ArrayList<String>();

    public static boolean pass(Request request) {
        if (whiteListApps.isEmpty()) {
            return true;
        }
        boolean r = whiteListApps.contains(RequestCtxUtil.getClientAppName());
        if (!r) {
            limitedLog(request);
        }
        return r;
    }

    static Logger logger = RpcLogger.getLogger();

    private static void limitedLog(Request request) {
        logger.error("[WhiteList Deny] {} invoke {}.{}",
                RequestCtxUtil.getClientAppName(), request.getServiceKey(), request.getMethod());
    }

}
