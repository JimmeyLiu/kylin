package org.kylin.protocol.address;

import org.kylin.common.AsyncCallback;

import java.net.URI;
import java.util.Set;

/**
 * Created by jimmey on 15-6-22.
 */
public interface AddressService {

    /**
     * @return
     */
    public int priority();

    public void lookup(String service, String version, AsyncCallback<Set<Address>> callback);

    public void register(String service, String version, String address);

    public void unregister(String server, String version, String address);
}
