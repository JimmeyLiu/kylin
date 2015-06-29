package org.kylin.transport.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kylin.common.log.RpcLogger;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.processor.RPCProcessor;
import org.slf4j.Logger;

/**
 * Created by jimmey on 15-6-25.
 */
public class MessageHandler extends ChannelInboundHandlerAdapter {
    RPCProcessor processor;
    Logger logger = RpcLogger.getLogger();

    public MessageHandler(RPCProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Request) {
            handleRequest((Request) msg, ctx);
        }
    }

    void handleRequest(Request request, final ChannelHandlerContext ctx) {
        processor.process(request, new RPCProcessor.Callback() {
            @Override
            public void on(Response response) {
                ctx.writeAndFlush(response);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("", cause);
    }
}
