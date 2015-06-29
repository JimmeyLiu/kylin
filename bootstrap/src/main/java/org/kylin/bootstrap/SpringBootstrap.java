package org.kylin.bootstrap;

import org.kylin.bootstrap.service.RPCProcessorImpl;
import org.kylin.bootstrap.service.ServiceFactory;
import org.kylin.common.util.IpUtils;
import org.kylin.spring.Consumer;
import org.kylin.spring.Provider;
import org.kylin.transport.netty.server.NettyServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jimmey on 15-6-26.
 */
public class SpringBootstrap implements BeanPostProcessor {

    private volatile AtomicBoolean inited = new AtomicBoolean(false);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            Consumer consumer = field.getAnnotation(Consumer.class);
            if (consumer == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object object = ServiceFactory.getService(field.getType(), consumer.version());
                field.set(bean, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Provider.class)) {
            if (inited.compareAndSet(false, true)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NettyServer server = new NettyServer(new RPCProcessorImpl());
                        server.listen(IpUtils.getLocalIp(), 10000);
                    }
                }).start();
            }
            Type[] types = bean.getClass().getGenericInterfaces();
            if (types.length > 0) {
                ServiceFactory.register((Class) types[0], bean, IpUtils.getLocalIp() + ":" + 10000);
            }
        }

        return bean;
    }
}
