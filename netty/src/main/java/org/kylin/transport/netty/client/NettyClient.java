package org.kylin.transport.netty.client;

import io.netty.channel.Channel;
import org.kylin.common.log.RpcLogger;
import org.kylin.protocol.address.Address;
import org.kylin.transport.Client;
import org.kylin.transport.TransportFuture;
import org.slf4j.Logger;

import java.net.URI;

/**
 * Created by jimmey on 15-6-25.
 */
public class NettyClient implements Client {

    Logger logger = RpcLogger.getLogger();

    Channel channel;
    Address address;

    public NettyClient(Address address, Channel channel) {
        this.channel = channel;
        this.address = address;
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public void doAsk(TransportFuture future) {
        channel.writeAndFlush(future);
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public Address address() {
        return address;
    }
}
