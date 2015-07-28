package org.kylin.address;

import java.util.List;

/**
 * Created by jimmey on 15-6-22.
 */
public interface AddressService {

    /**
     * @return
     */
    public int priority();

    public void lookup(String serviceKey, Callback callback);

    public void register(String serviceKey, String address);

    public void unregister(String serverKey, String address);

    public interface Callback {
        void on(List<Address> addresses);
    }
}
