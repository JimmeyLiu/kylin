package org.kylin.transport.netty.server.restful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.kylin.common.util.ReflectUtils;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.common.util.StringUtils;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;

import java.lang.reflect.Type;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by jimmey on 15-7-30.
 */
public class RestfulHandler extends ChannelDuplexHandler {
    static String CONTENT_TYPE = "Content-Type";
    static String CONTENT_LENGTH = "Content-Length";
    static String CONNECTION = "Connection";
    static String CONTENT_JSON = "application/json; charset=UTF-8";
    static String KEEP_ALIVE = "Keep-Alive";
    static String CLIENT_APP_KEY = "Client-App";

    static String[] EMPTY_TYPES = new String[0];
    static Object[] EMPTY_ARGS = new Object[0];

    Request request;
    HttpRequest httpRequest;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Response) {
            writeResponse((Response) msg, ctx);
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            this.httpRequest = (HttpRequest) msg;
            this.request = new Request(0);
            // /RPC/org.kylin.test.service.TestService:1.0.0/say/
            String uri = httpRequest.getUri();
            int i = uri.indexOf("?");
            if (i > 0) {
                uri = uri.substring(0, i);
            }
            String[] v = uri.split("/");
            if (v.length < 4 || v.length > 5) {
                errorResponse(StatusCode.REST_BAD_REQUEST, ctx);
                return;
            }
            request.setServiceKey(v[2]);
            request.setMethod(v[3]);
            String[] argTypes = EMPTY_TYPES;
            if (v.length == 5) {
                argTypes = StringUtils.split(v[4], ',');
            }
            request.setArgTypes(argTypes);

            HttpHeaders headers = httpRequest.headers();
            String app = headers.get(CLIENT_APP_KEY);
            if (app != null) {
                request.putContext(RequestCtxUtil.CLIENT_APP_NAME, app);
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf byteBuf = content.content();
            Object[] args = EMPTY_ARGS;
            if (byteBuf.isReadable() && request.getArgTypes().length > 0) {
                try {
                    Type[] types = ReflectUtils.getTypes(request.getArgTypes());

                    List<Object> list = JSON.parseArray(byteBuf.toString(CharsetUtil.UTF_8));
                    if (list != null && list.size() != types.length) {
                        errorResponse(StatusCode.REST_PARAM_ERROR, ctx);
                        return;
                    }
                    if (list != null) {
                        args = new Object[types.length];
                        for (int i = 0; i < types.length; i++) {
                            args[i] = TypeUtils.cast(list.get(i), types[i], ParserConfig.getGlobalInstance());
                        }
                    }
                } catch (Exception e) {
                    errorResponse(StatusCode.REST_ARGTYPE_ERROR, ctx);
                    return;
                }
            }

            request.setArgs(args);
            ctx.fireChannelRead(request);
        }
    }


    private void errorResponse(StatusCode statusCode, ChannelHandlerContext ctx) {
        Response response = new Response(0);
        response.setStatus(statusCode);
        writeResponse(response, ctx);
    }

    private void writeResponse(Response response, ChannelHandlerContext ctx) {
        RestResponse rpcResponse = new RestResponse(response.getStatus(), response.getException(), response.getResult());
        ByteBuf byteBuf = Unpooled.copiedBuffer(JSON.toJSONBytes(rpcResponse));
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
        HttpHeaders.addHeader(httpResponse, CONTENT_TYPE, CONTENT_JSON);
        boolean keepAlive = HttpHeaders.isKeepAlive(httpRequest);
        if (keepAlive) {
            HttpHeaders.addHeader(httpResponse, CONTENT_LENGTH, httpResponse.content().readableBytes());
            HttpHeaders.addHeader(httpResponse, CONNECTION, KEEP_ALIVE);
        }
        ChannelFuture future = ctx.writeAndFlush(httpResponse);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
