package org.kylin.bootstrap;

import org.kylin.bootstrap.service.RPCProcessorImpl;
import org.kylin.bootstrap.service.ServiceBean;
import org.kylin.bootstrap.service.ServiceFactory;
import org.kylin.bootstrap.service.ServiceProxy;
import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.IpUtils;
import org.kylin.spring.Consumer;
import org.kylin.spring.Provider;
import org.kylin.transport.netty.server.NettyServer;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
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

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map map = beanFactory.getBeansOfType(ServiceBean.class);
        for (Object object : map.values()) {
            ServiceBean bean = (ServiceBean) object;
            ServiceProxy proxy = new ServiceProxy(bean.getService(), bean.getVersion());
            try {
                ServiceFactory.putConsumerService(Class.forName(bean.getService()), proxy);
            } catch (Exception e) {
                logger.error("get class error {}", bean.getService());
            }
        }
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
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
                logger.error("set consumer error {}", field.getType());
            }
        }
        return bean;
    }

}
