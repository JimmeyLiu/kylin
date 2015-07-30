package org.kylin.common.util;

/**
 * Created by jimmey on 15-7-28.
 */
public class Config {
    private static boolean online;

    static {
        //默认为online环境
        online = Boolean.valueOf(System.getProperty("kylin.online", "true"));
    }

    public static String getAppName() {
        return System.getProperty("kylin.appName");
    }

    public static int getKylinPort() {
        return Integer.getInteger("kylin.port", 18000);
    }

    public static int getConsolePort() {
        return getKylinPort() + 1;
    }


    public static boolean isOnline() {
        return online;
    }

    public static int getIoWorkers() {
        return Integer.valueOf(System.getProperty("io.workers", Runtime.getRuntime().availableProcessors() * 2 + ""));
    }

    public static boolean graceful() {
        return Boolean.getBoolean("kylin.graceful");
    }
}
