package org.kylin.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.Config;
import org.kylin.protocol.processor.RPCProcessor;
import org.kylin.transport.Server;
import org.kylin.transport.netty.handler.BlockCodecHandler;
import org.kylin.transport.netty.handler.MessageCodecHandler;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

/**
 * Created by jimmey on 15-6-25.
 */
public class NettyServer implements Server {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("RPC-BOSS"));
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(Config.getIoWorkers(), new DefaultThreadFactory("RPC-WORKER"));
    Logger logger = RpcLogger.getLogger();
    RPCProcessor processor;

    public NettyServer(RPCProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void close() {

    }

    @Override
    public void listen(String host, int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 可配置的几个
        bootstrap.group(bossGroup, workerGroup)//
                .channel(NioServerSocketChannel.class)//
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)//
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)//
                .childOption(ChannelOption.TCP_NODELAY, true)//
                .childOption(ChannelOption.SO_REUSEADDR, true)
                        // .childOption(ChannelOption.SO_KEEPALIVE, true)//
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                        pipeline.addLast("codec", new BlockCodecHandler());
                        pipeline.addLast("message", new MessageCodecHandler());
                        pipeline.addLast("handler", new MessageHandler(processor));
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
