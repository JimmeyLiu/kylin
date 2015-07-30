package org.kylin.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmey on 15-7-28.
 */
public class RequestCtxUtil {

    private static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>();

    public static String CLIENT_APP_NAME = "clientAppName";
    public static String CLIENT_IP = "clientIp";
    public static String TARGET_SERVER_IP = "targetServerIP";


    public static String getClientAppName() {
        return get().get(CLIENT_APP_NAME);
    }

    public static String getClientIp() {
        return get().get(CLIENT_IP);
    }

    public static String getLocalIp() {
        return IpUtils.getLocalIp();
    }

    private static Map<String, String> get() {
        Map<String, String> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, String>(2);
            threadLocal.set(map);
        }
        return map;
    }

    public static void setTargetServer(String address) {
        get().put(TARGET_SERVER_IP, address);
    }

    public static String getTargetServerIp() {
        return get().get(TARGET_SERVER_IP);
    }

    public static void putContext(Map<String, String> map) {
        get().putAll(map);
    }


}
