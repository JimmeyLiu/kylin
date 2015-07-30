package org.kylin.address;

import org.kylin.config.ConfigFactory;
import org.kylin.config.Listener;

/**
 * Created by jimmey on 15-7-30.
 */
public class ConfigAddressService extends WeightAddressService {

    @Override
    protected void doLookup(String serviceKey, final Callback callback) {
        ConfigFactory.getConfig(serviceKey, new Listener() {
            @Override
            public void on(String content) {

            }
        });
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public void register(String serviceKey, String address) {
        ConfigFactory.append(serviceKey, address);
    }

    @Override
    public void unregister(String serverKey, String address) {
        ConfigFactory.remove(serverKey, address);
    }
}
