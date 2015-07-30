package org.kylin.config;

/**
 * Created by jimmey on 15-7-28.
 */
public interface ConfigService {

    void getConfig(String key, Listener listener);

    void append(String key, String content);

    void remove(String key, String content);
}
