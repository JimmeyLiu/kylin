package org.kylin.bootstrap;

import org.kylin.bootstrap.annotation.Consumer;
import org.kylin.bootstrap.annotation.Provider;
import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.Config;
import org.kylin.common.util.IpUtils;
import org.kylin.processor.RPCProcessorImpl;
import org.kylin.processor.service.ServiceFactory;
import org.kylin.transport.netty.server.NettyServer;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jimmey on 15-6-25.
 */
public class Kylin {

    private static final AtomicBoolean init = new AtomicBoolean(false);
    private static Logger logger = RpcLogger.getLogger();

    public static void provider(Object object) {
        if (init.compareAndSet(false, true)) {
            new NettyServer(new RPCProcessorImpl()).listen(IpUtils.getLocalIp(), Config.getKylinPort());
        }
        Type[] types = object.getClass().getGenericInterfaces();
        if (types.length > 0) {
            Provider provider = object.getClass().getAnnotation(Provider.class);
            ServiceFactory.register((Class) types[0], provider.version(), object);
        }
    }

    public static void consumer(Object bean) {
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
    }


}
