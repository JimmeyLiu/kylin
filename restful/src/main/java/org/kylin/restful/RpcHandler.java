package org.kylin.restful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.kylin.common.util.ReflectUtils;
import org.kylin.common.util.StringUtils;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;
import org.kylin.protocol.processor.RPCProcessor;

import java.lang.reflect.Type;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by jimmey on 15-7-29.
 */
public class RpcHandler extends SimpleChannelInboundHandler<Object> {
    static String CONTENT_TYPE = "Content-Type";
    static String CONTENT_LENGTH = "Content-Length";
    static String CONNECTION = "Connection";
    static String CONTENT_JSON = "application/json";
    static String CLOSE = "Close";

    static String[] EMPTY_TYPES = new String[0];
    static Object[] EMPTY_ARGS = new Object[0];

    RPCProcessor processor;
    Request request;
    HttpRequest httpRequest;

    public RpcHandler(RPCProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {
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
            processor.process(request, new RPCProcessor.Callback() {
                @Override
                public void on(Response response) {
                    writeResponse(response, ctx);
                }
            });
        }
    }


    private void errorResponse(StatusCode statusCode, ChannelHandlerContext ctx) {
        Response response = new Response(0);
        response.setStatus(statusCode);
        writeResponse(response, ctx);
    }

    private void writeResponse(Response response, ChannelHandlerContext ctx) {
        RpcResponse rpcResponse = new RpcResponse(response.getStatus(), response.getException(), response.getResult());
        ByteBuf byteBuf = Unpooled.copiedBuffer(JSON.toJSONBytes(rpcResponse));
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
        HttpHeaders.addHeader(httpResponse, CONTENT_TYPE, CONTENT_JSON);
        HttpHeaders.addHeader(httpResponse, CONTENT_LENGTH, httpResponse.content().readableBytes());
        if (!HttpHeaders.isKeepAlive(httpRequest)) {
            HttpHeaders.addHeader(httpResponse, CONNECTION, CLOSE);
        }
        ctx.writeAndFlush(httpResponse);
    }
}
