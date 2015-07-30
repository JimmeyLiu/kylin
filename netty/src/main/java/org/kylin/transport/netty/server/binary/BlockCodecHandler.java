package org.kylin.transport.netty.server.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.kylin.protocol.codec.BinaryCodec;
import org.kylin.protocol.codec.Block;
import org.kylin.protocol.codec.Codec;

import java.util.List;

/**
 * Created by jimmey on 15-6-23.
 */
public class BlockCodecHandler extends ByteToMessageCodec<Block> {

    BinaryCodec codec = new BinaryCodec();

    @Override
    protected void encode(ChannelHandlerContext ctx, Block msg, final ByteBuf out) throws Exception {
        codec.encode(msg, new Codec.Output() {
            @Override
            public void write(byte b) {
                out.writeByte(b);
            }

            @Override
            public void writeInt(int i) {
                out.writeInt(i);
            }

            @Override
            public void writeBytes(byte[] bytes) {
                out.writeBytes(bytes);
            }

            @Override
            public void writeLong(long l) {
                out.writeLong(l);
            }

        });
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        codec.decode(new Codec.Input() {
            @Override
            public byte read() {
                return in.readByte();
            }

            @Override
            public int readInt() {
                return in.readInt();
            }

            @Override
            public long readLong() {
                return in.readLong();
            }

            @Override
            public byte[] readBytes(int len) {
                byte[] bytes = new byte[len];
                in.readBytes(bytes);
                return bytes;
            }

            @Override
            public int readableBytes() {
                return in.readableBytes();
            }
        }, new Codec.Callback<Block>() {
            @Override
            public void on(Block block) {
                out.add(block);
            }
        });
    }
}
