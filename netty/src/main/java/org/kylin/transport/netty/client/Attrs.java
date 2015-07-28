package org.kylin.transport.netty.client;

import io.netty.util.AttributeKey;
import org.kylin.transport.Client;

/**
 * Created by jimmey on 15-7-28.
 */
public interface Attrs {
    AttributeKey<Client> CLIENT_ATTRIBUTE_KEY = AttributeKey.valueOf("CLIENT");
}
