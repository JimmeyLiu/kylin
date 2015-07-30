package org.kylin.transport.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.kylin.protocol.codec.BinaryCodec;
import org.kylin.transport.netty.server.binary.BlockCodecHandler;
import org.kylin.transport.netty.server.binary.MessageCodecHandler;
import org.kylin.transport.netty.server.restful.RestfulHandler;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jimmey on 15-7-30.
 * 通过前4个字节判断
 */
public class JudgeHandler extends ChannelInboundHandlerAdapter {


    private AtomicBoolean known = new AtomicBoolean(false);
    static final byte[] HTTP = "POST".getBytes();
    static final int LEN = 4;
    static final Exception BAD_PACKET = new Exception("Bad Kylin Packet");


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!known.get()) {
            ByteBuf byteBuf = (ByteBuf) msg;
            if (byteBuf.readableBytes() > LEN) {
                byte[] bytes = new byte[LEN];
                byteBuf.getBytes(0, bytes);
                ChannelPipeline p = ctx.channel().pipeline();
                if (Arrays.equals(HTTP, bytes)) {
                    p.addAfter("judge", "decoder", new HttpRequestDecoder());
                    p.addAfter("decoder", "aggregator", new HttpObjectAggregator(1048576));
                    p.addAfter("aggregator", "encoder", new HttpResponseEncoder());
                    p.addAfter("encoder", "rest", new RestfulHandler());
                } else if (Arrays.equals(BinaryCodec.MAGIC, bytes)) {
                    p.addAfter("judge", "codec", new BlockCodecHandler());
                    p.addAfter("codec", "message", new MessageCodecHandler());
                } else {
                    ctx.fireExceptionCaught(BAD_PACKET);
                }
                known.set(true);
            }
        }
        super.channelRead(ctx, msg);
    }
}
