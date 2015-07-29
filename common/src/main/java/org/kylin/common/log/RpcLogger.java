package org.kylin.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jimmey on 15-6-23.
 */
public class RpcLogger {

    public static Logger getLogger() {
        return LoggerFactory.getLogger("kylin");
    }

    public static Logger getAccessLogger() {
        return LoggerFactory.getLogger("kylin-access");
    }

}
