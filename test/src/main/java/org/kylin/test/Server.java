package org.kylin.test;

import org.kylin.bootstrap.service.RPCProcessorImpl;
import org.kylin.bootstrap.service.ServiceFactory;
import org.kylin.test.service.TestService;
import org.kylin.test.service.TestServiceImpl;
import org.kylin.transport.netty.server.NettyServer;

/**
 * Created by jimmey on 15-6-23.
 */
public class Server {

    public static void main(String[] args) throws Exception {
        NettyServer server = new NettyServer(new RPCProcessorImpl());
        ServiceFactory.register(TestService.class, new TestServiceImpl(), "127.0.0.1:10000");
        server.listen("127.0.0.1", 10000);
    }

}
