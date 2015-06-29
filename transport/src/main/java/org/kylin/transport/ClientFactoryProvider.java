package org.kylin.transport;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by jimmey on 15-6-25.
 */
public class ClientFactoryProvider {
    static ClientFactory factory;

    static {
        ServiceLoader<ClientFactory> loader = ServiceLoader.load(ClientFactory.class);
        Iterator<ClientFactory> it = loader.iterator();
        while (it.hasNext()) {
            factory = it.next();
        }
    }

    public static ClientFactory get() {
        return factory;
    }
}
