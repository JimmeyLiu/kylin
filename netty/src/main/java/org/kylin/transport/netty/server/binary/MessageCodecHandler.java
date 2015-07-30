package org.kylin.transport.netty.server.binary;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.kylin.protocol.codec.Block;
import org.kylin.protocol.codec.MessageCodec;
import org.kylin.protocol.message.Message;

import java.util.List;

/**
 * Created by jimmey on 15-6-23.
 */
public class MessageCodecHandler extends MessageToMessageCodec<Block, Message> {

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        MessageCodec codec = MessageCodec.getCodec(msg.getType().ordinal());
        if (codec == null) {
            return;
        }
        out.add(codec.encode(msg));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Block msg, List<Object> out) throws Exception {
        MessageCodec codec = MessageCodec.getCodec(msg.getHead().getType());
        out.add(codec.decode(msg));
    }
}
