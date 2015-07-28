package org.kylin.transport.netty.server;

import org.kylin.protocol.processor.RPCProcessor;

/**
 * Created by jimmey on 15-7-28.
 */
public class RestServer {

    RPCProcessor processor;

    public RestServer(RPCProcessor processor) {
        this.processor = processor;
    }



}
