package org.kylin.bootstrap;

import org.kylin.bootstrap.annotation.Consumer;
import org.kylin.bootstrap.annotation.Provider;
import org.kylin.bootstrap.bean.ConsumerBean;
import org.kylin.bootstrap.bean.ProviderBean;
import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.Config;
import org.kylin.common.util.IpUtils;
import org.kylin.processor.RPCProcessorImpl;
import org.kylin.processor.service.ServiceFactory;
import org.kylin.processor.service.ServiceProxy;
import org.kylin.protocol.processor.RPCProcessor;
import org.kylin.transport.netty.server.Console;
import org.kylin.transport.netty.server.NettyServer;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jimmey on 15-6-26.
 */
public class SpringBootstrap implements BeanPostProcessor, BeanFactoryPostProcessor {

    private volatile AtomicBoolean inited = new AtomicBoolean(false);

    Logger logger = RpcLogger.getLogger();

    RPCProcessor processor;

    public SpringBootstrap() {
        this.processor = new RPCProcessorImpl();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map map = beanFactory.getBeansOfType(ProviderBean.class);
        for (Object object : map.values()) {
            ProviderBean bean = (ProviderBean) object;
            try {
                Class<?> clazz = Class.forName(bean.getService());
                ServiceFactory.register(clazz, bean.getVersion(), bean.getTarget());
            } catch (Exception e) {
                logger.error("service class not found " + bean.getService(), e);
                throw new RuntimeException(e.getMessage());
            }
        }

        map = beanFactory.getBeansOfType(ConsumerBean.class);
        for (Object key : map.keySet()) {
            ConsumerBean bean = (ConsumerBean) map.get(key);
            try {
                Class<?> clazz = Class.forName(bean.getService());
                ServiceProxy proxy = new ServiceProxy(bean.getService(), bean.getVersion(), bean.getTarget());
                Object p = ServiceFactory.createProxy(clazz, proxy);
                ServiceFactory.putConsumerService(clazz, bean.getVersion(), p);
            } catch (Exception e) {
                logger.error("service class not found " + bean.getService(), e);
                throw new RuntimeException(e.getMessage());
            }
        }

        Console.start();
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Provider.class)) {
            if (inited.compareAndSet(false, true)) {
                new NettyServer(processor).listen(IpUtils.getLocalIp(), Config.getKylinPort());
            }
            Type[] types = bean.getClass().getGenericInterfaces();
            if (types.length > 0) {
                Provider provider = bean.getClass().getAnnotation(Provider.class);
                ServiceFactory.register((Class) types[0], provider.version(), bean);
            }
        }

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
                logger.error("set consumer error", e);
            }
        }
        return bean;
    }

}
