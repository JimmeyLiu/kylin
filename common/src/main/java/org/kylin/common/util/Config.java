package org.kylin.common.util;

/**
 * Created by jimmey on 15-7-28.
 */
public class Config {

    public static String getAppName() {
        return System.getProperty("kylin.appName", "UNKNOWN");
    }

    public static int getKylinPort() {
        return Integer.getInteger("kylin.port", 18000);
    }

    public static int getHttpPort() {
        return getKylinPort() + 1;
    }

    public static int getTelnetPort() {
        return getKylinPort() + 2;
    }

    public static int getIoWorkers() {
        return Integer.valueOf(System.getProperty("io.workers", Runtime.getRuntime().availableProcessors() * 2 + ""));
    }

    public static boolean graceful() {
        return Boolean.getBoolean("kylin.graceful");
    }
}
