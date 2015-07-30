package org.kylin.processor.service;

import org.kylin.common.util.Config;
import org.kylin.common.util.IpUtils;
import org.kylin.address.Address;
import org.kylin.address.AddressFactory;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jimmey on 15-6-22.
 */

public class ServiceFactory {

    private static ConcurrentHashMap<String, Object> providerServices = new ConcurrentHashMap<String, Object>();
    private static ConcurrentHashMap<String, Object> consumerServices = new ConcurrentHashMap<String, Object>();

    private static ReentrantLock lock = new ReentrantLock();

    public static String getServiceKey(Class<?> service, String version) {
        return service.getName() + ":" + version;
    }

    public static String getServiceKey(String service, String version) {
        return service + ":" + version;
    }

    public static String getServiceClass(String serviceKey) {
        return serviceKey.split(":")[0];
    }

    @SuppressWarnings("all")
    public static <T> T getService(Class<T> service, String version) {
        String key = getServiceKey(service, version);
        Object object = consumerServices.get(key);
        if (object == null) {
            lock.lock();
            try {
                object = consumerServices.get(key);
                if (object == null) {
                    object = createProxy(service, new ServiceProxy(service.getName(), version));
                    consumerServices.put(key, object);
                }
            } finally {
                lock.unlock();
            }
        }
        return (T) object;
    }

    public static Object createProxy(Class<?> service, ServiceProxy proxy) {
        return Proxy.newProxyInstance(ServiceFactory.class.getClassLoader(), new Class[]{service}, proxy);
    }

    public static void putConsumerService(Class<?> service, String version, Object object) {
        consumerServices.putIfAbsent(getServiceKey(service, version), object);
    }

    public static List<String> getConsumers() {
        return new ArrayList<String>(consumerServices.keySet());
    }

    public static List<String> getProviders() {
        return new ArrayList<String>(providerServices.keySet());
    }

    static String ADDRESS = "";

    static {
        Address address = new Address();
        address.setUri(URI.create("tcp://" + IpUtils.getLocalIp() + ":" + Config.getKylinPort()));
        ADDRESS = address.toString();
    }

    /**
     * @param service
     * @param object
     * @param <T>
     */
    public static <T> void register(Class<T> service, String version, Object object) {
        String key = getServiceKey(service, version);
        providerServices.put(key, object);
        AddressFactory.getAddressService().register(key, ADDRESS);
    }

    public static Object getLocalService(String serviceKey) {
        return providerServices.get(serviceKey);
    }


}
