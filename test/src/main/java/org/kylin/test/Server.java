package org.kylin.test;

import org.kylin.processor.handler.RPCProcessorImpl;
import org.kylin.processor.service.ServiceFactory;
import org.kylin.test.service.TestService;
import org.kylin.test.service.TestServiceImpl;
import org.kylin.transport.netty.server.NettyServer;

/**
 * Created by jimmey on 15-6-23.
 */
public class Server {

    public static void main(String[] args) throws Exception {
        NettyServer server = new NettyServer(new RPCProcessorImpl());
        ServiceFactory.register(TestService.class, "1.0.0", new TestServiceImpl());
        server.listen("127.0.0.1", 10000);
    }

}
