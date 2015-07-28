package org.kylin.transport.netty.server.rest;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.processor.RPCProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jimmey on 15-7-28.
 */
public class RestHandler extends ChannelHandlerAdapter {

    RPCProcessor processor;

    static Pattern pattern = Pattern.compile("/RPC/(\\w+?)/(\\w+?)");

    public RestHandler(RPCProcessor processor) {
        this.processor = processor;
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            String path = httpRequest.getUri();
            String[] v = path.split("/");
            if (v.length != 4 || !"RPC".equals(v[1])) {
                //bad request
                return;
            }
            Request request = new Request(0);

//            Param param = htt
        }
    }

    public static void main(String[] args) {
        String str = "/RPC/org.kylin.test.service.TestService:1.0.0/hello";
        Matcher m = pattern.matcher(str);
        System.out.println(m.find());
        if (m.matches()) {
            System.out.println("s");
        }
    }
}
