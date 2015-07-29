package org.kylin.common.util;

import java.net.InetAddress;

/**
 * Created by jimmey on 15-6-26.
 */
public class IpUtils {

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

}
