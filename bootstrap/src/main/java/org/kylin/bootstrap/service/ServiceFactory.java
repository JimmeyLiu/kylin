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

    private static Map<Class<?>, Object> localServices = new ConcurrentHashMap<Class<?>, Object>();
    private static Map<Class<?>, Object> proxyServices = new ConcurrentHashMap<Class<?>, Object>();

    private static ReentrantLock lock = new ReentrantLock();

    public static <T> T getService(Class<T> service) {
        return getService(service, "");
    }

    @SuppressWarnings("all")
    public static <T> T getService(Class<T> service, String version) {
        Object object = getLocalService(service);
        if (object != null) {
            return (T) object;
        }
        object = proxyServices.get(service);
        if (object == null) {
            lock.lock();
            try {
                object = proxyServices.get(service);
                if (object == null) {
                    object = Proxy.newProxyInstance(ServiceFactory.class.getClassLoader(), new Class[]{service}, new ServiceProxy(service.getName(), version));
                    proxyServices.put(service, object);
                }
            } finally {
                lock.unlock();
            }
        }
        return (T) object;
    }

    @SuppressWarnings("all")
    public static <T> T getLocalService(Class<T> service) {
        return (T) localServices.get(service);
    }


    /**
     * 注册服务到配置中心，同时存储本地
     *
     * @param service
     * @param object
     * @param <T>
     */
    public static <T> void register(Class<T> service, Object object, String address) {
        localServices.put(service, object);
        AddressFactory.getAddressService().register(service.getName(), "1.0.0", address);
    }

}
