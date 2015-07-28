package org.kylin.address;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by jimmey on 15-6-22.
 */
public class AddressFactory {

    static AddressService addressService;

    static {
        ServiceLoader<AddressService> loader = ServiceLoader.load(AddressService.class);
        Iterator<AddressService> it = loader.iterator();
        while (it.hasNext()) {
            AddressService serialize = it.next();
            if (addressService == null || addressService.priority() < serialize.priority()) {
                addressService = serialize;
            }
        }
    }

    public static AddressService getAddressService() {
        return addressService;
    }


}
