package org.kylin.config;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by jimmey on 15-7-28.
 */
public class ConfigFactory {

    static ConfigService configService;

    static {
        ServiceLoader<ConfigService> loader = ServiceLoader.load(ConfigService.class);
        Iterator<ConfigService> it = loader.iterator();
        while (it.hasNext()) {
            configService = it.next();
        }
    }

    public static void getConfig(String key, Listener listener) {
        configService.getConfig(key, listener);
    }

    public static void append(String key, String content) {
        configService.append(key, content);
    }

    public static void remove(String key, String content) {
        configService.remove(key, content);
    }

}
