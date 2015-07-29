package org.kylin.restful;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.kylin.common.log.RpcLogger;
import org.kylin.protocol.processor.RPCProcessor;
import org.kylin.transport.Server;
import org.slf4j.Logger;

import java.net.InetSocketAddress;


/**
 * Created by jimmey on 15-7-29.
 */
public class RestfulServer implements Server {

    RPCProcessor processor;
    Logger logger = RpcLogger.getLogger();

    public RestfulServer(RPCProcessor processor) {
        this.processor = processor;
    }


    @Override
    public void close() {

    }

    @Override
    public void listen(String host, int port) {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpRequestDecoder());
                        // Uncomment the following line if you don't want to handle HttpChunks.
                        //p.addLast(new HttpObjectAggregator(1048576));
                        p.addLast(new HttpResponseEncoder());
                        // Remove the following line if you don't want automatic content compression.
                        //p.addLast(new HttpContentCompressor());
                        p.addLast(new RpcHandler(processor));
                    }
                });
        long tryBind = 3;
        while (tryBind > 0) {
            ChannelFuture cf = bootstrap.bind(new InetSocketAddress(host, port));
            try {
                cf.await();
                if (cf.isSuccess()) {
                    logger.warn("Server started,listen at: " + port);
                    return;
                } else {
                    tryBind--;
                    if (tryBind <= 0) {
                        logger.warn("After 3 failed attempts to start server at port : " + port
                                + ", we are shutting down the vm");
                        System.exit(1);
                    } else {
                        logger.warn("Failed to start server at port : " + port + ", Sleep 3s and try again",
                                cf.cause());
                        Thread.sleep(3000);
                    }
                }
            } catch (Exception e) {
                logger.error("start netty server error", e);
            }
        }

    }


}
