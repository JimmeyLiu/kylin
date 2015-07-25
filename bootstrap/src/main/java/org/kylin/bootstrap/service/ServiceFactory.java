package org.kylin.bootstrap.service;

import org.kylin.protocol.address.AddressFactory;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jimmey on 15-6-22.
 */

public class ServiceFactory {

    private static ConcurrentHashMap<Class<?>, Object> providerServices = new ConcurrentHashMap<Class<?>, Object>();
    private static ConcurrentHashMap<Class<?>, Object> consumerServices = new ConcurrentHashMap<Class<?>, Object>();
    private static final String DEFAULT_VERSION = "1.0.0";
    private static ReentrantLock lock = new ReentrantLock();

    @SuppressWarnings("all")
    public static <T> T getService(Class<T> service, String version) {
        Object object = getLocalService(service);
        if (object != null) {
            return (T) object;
        }
        object = consumerServices.get(service);
        if (object == null) {
            lock.lock();
            try {
                object = consumerServices.get(service);
                if (object == null) {
                    object = Proxy.newProxyInstance(ServiceFactory.class.getClassLoader(), new Class[]{service}, new ServiceProxy(service.getName(), version));
                    consumerServices.put(service, object);
                }
            } finally {
                lock.unlock();
            }
        }
        return (T) object;
    }

    public static void putConsumerService(Class<?> service, Object object) {
        consumerServices.putIfAbsent(service, object);
    }

    @SuppressWarnings("all")
    public static <T> T getLocalService(Class<T> service) {
        return (T) providerServices.get(service);
    }


    /**
     * 注册服务到配置中心，同时存储本地
     *
     * @param service
     * @param object
     * @param <T>
     */
    public static <T> void register(Class<T> service, Object object, String address) {
        providerServices.put(service, object);
        AddressFactory.getAddressService().register(service.getName(), DEFAULT_VERSION, address);
    }

}
