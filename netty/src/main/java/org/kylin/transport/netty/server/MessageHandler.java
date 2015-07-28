package org.kylin.transport.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.processor.RPCProcessor;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmey on 15-6-25.
 */
public class MessageHandler extends ChannelInboundHandlerAdapter {
    RPCProcessor processor;
    Logger logger = RpcLogger.getLogger();
    Map<String, String> clientContext;

    public MessageHandler(RPCProcessor processor) {
        this.processor = processor;
        this.clientContext = new HashMap<String, String>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        clientContext.put(RequestCtxUtil.CLIENT_IP, ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Request) {
            handleRequest((Request) msg, ctx);
        }
    }

    void handleRequest(Request request, final ChannelHandlerContext ctx) {
        Map<String, String> requestContext = request.getContext();
        if (requestContext != null) {
            RequestCtxUtil.putContext(requestContext);
            String app = requestContext.remove(RequestCtxUtil.CLIENT_APP_NAME);
            if (app != null) {
                clientContext.put(RequestCtxUtil.CLIENT_APP_NAME, app);
            }
        }
        RequestCtxUtil.putContext(clientContext);
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
