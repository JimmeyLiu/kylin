package org.kylin.transport.netty.client;

import io.netty.channel.*;
import org.kylin.common.log.RpcLogger;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.transport.TransportFuture;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmey on 15-6-25.
 */
public class MessageHandler extends ChannelDuplexHandler {
    Logger logger = RpcLogger.getLogger();

    Map<Long, TransportFuture> pending = new HashMap<Long, TransportFuture>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Response) {
            Response response = (Response) msg;
            TransportFuture future = pending.remove(response.getMid());
            if (future != null) {
                future.set(response);
            } else {
                logger.error("response {} match no TransportFuture", response.getMid());
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof TransportFuture) {
            send(ctx, (TransportFuture) msg, promise);
        }
    }

    private void send(final ChannelHandlerContext ctx, final TransportFuture future, ChannelPromise promise) {
        final Request request = future.getRequest();
        ChannelFuture writeFuture = ctx.writeAndFlush(request, promise);

        writeFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    pending.put(request.getMid(), future);
                } else {
                    Response response = Response.errorResponse(request.getSerializeType(), 600, channelFuture.cause().getMessage());
                    future.set(response);
                    if (!ctx.channel().isActive()) {
//                        factory.remove(ctx.channel());
                    }
                }
            }
        });
    }
}
