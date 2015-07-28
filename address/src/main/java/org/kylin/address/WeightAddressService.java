package org.kylin.address;

import com.alibaba.fastjson.JSON;
import org.kylin.address.weight.WeightRule;
import org.kylin.config.ConfigFactory;
import org.kylin.config.Listener;

import java.util.List;

/**
 * Created by jimmey on 15-7-28.
 */
public abstract class WeightAddressService implements AddressService {

    List<Address> last;
    WeightRule weightRule;

    @Override
    public void lookup(String serviceKey, final Callback callback) {
        ConfigFactory.getConfig(serviceKey + "_Weight", new Listener() {
            @Override
            public void on(String content) {
                weightRule = JSON.parseObject(content, WeightRule.class);
                if (last != null) {
                    callback.on(weightRule.refresh(last));
                }
            }
        });
        doLookup(serviceKey, new Callback() {
            @Override
            public void on(List<Address> addresses) {
                //处理权重逻辑
                last = addresses;
                if (weightRule != null) {
                    callback.on(weightRule.refresh(addresses));
                } else {
                    callback.on(addresses);
                }
            }
        });
    }

    protected abstract void doLookup(String serviceKey, Callback callback);

}
