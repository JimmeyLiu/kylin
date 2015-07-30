package org.kylin.transport.netty.client;

import io.netty.channel.*;
import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.Config;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Control;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;
import org.kylin.transport.Client;
import org.kylin.transport.TransportFuture;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jimmey on 15-6-25.
 */
public class MessageHandler extends ChannelDuplexHandler {
    Logger logger = RpcLogger.getLogger();

    Map<Integer, TransportFuture> pending = new HashMap<Integer, TransportFuture>();
    Client.Listener[] listeners;

    public MessageHandler(Client.Listener... listeners) {
        this.listeners = listeners;
    }

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
        } else if (msg instanceof Control) {
            Control control = (Control) msg;
            if (control.isHeartbeat()) {
                return;
            }
            if (control.isOffline()) {
                for (Client.Listener listener : listeners) {
                    listener.onServerOffline(ctx.channel().attr(Attrs.CLIENT_ATTRIBUTE_KEY).get());
                }
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof TransportFuture) {
            send(ctx, (TransportFuture) msg, promise);
        }
    }

    private AtomicBoolean HANDSHAKE = new AtomicBoolean(false);

    private void send(final ChannelHandlerContext ctx, final TransportFuture future, ChannelPromise promise) {
        Request request = future.getRequest();
        //只在第一个请求将AppName传输到服务端
        if (HANDSHAKE.compareAndSet(false, true)) {
            request.putContext(RequestCtxUtil.CLIENT_APP_NAME, Config.getAppName());
        }
        ChannelFuture writeFuture = ctx.writeAndFlush(request, promise);
        final Request req = request;
        writeFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    pending.put(req.getMid(), future);
                } else {
                    Response response = Response.errorResponse(req.getSerializeType(), StatusCode.CLIENT_SEND_ERROR);
                    future.set(response);
                }
            }
        });
    }
}
