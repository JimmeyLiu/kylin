package org.kylin.protocol.codec;

import java.util.Arrays;

/**
 * Created by jimmey on 15-6-22.
 */
public class BinaryCodec implements Codec {

    static final byte[] RESERVED = new byte[]{0};
    public static final byte[] MAGIC = "KLTP".getBytes();
    Block last = null;
    static final Exception BAD_PACKET = new Exception("Bad Kylin Packet");

    @Override
    public void encode(Block block, Output output) {
        output.writeBytes(MAGIC);
        output.write((byte) block.getHead().getVersion());
        output.write((byte) block.getHead().getType());
        output.write((byte) block.getHead().getSerializeType());
        output.writeBytes(RESERVED);
        output.writeInt(block.getHead().getMid());
        output.writeInt(block.getHead().getLength());
        if (block.getPayload() != null) {
            output.writeBytes(block.getPayload());
        }
    }

    @Override
    public void decode(Input input, Callback<Block> out) throws Exception {
        for (; ; ) {
            if (last != null) {
                if (input.readableBytes() < last.getHead().getLength()) {
                    return;
                }
                byte[] bytes = input.readBytes(last.getHead().getLength());
                last.setPayload(bytes);
                out.on(last);
                last = null;
            } else {
                if (input.readableBytes() < Block.HEAD_SIZE) {
                    return;
                }
                //KYLN
                if (!Arrays.equals(input.readBytes(4), MAGIC)) {
                    throw BAD_PACKET;
                }
                int ver = input.read();
                int type = input.read();
                int serializeType = input.read();
                input.read();//skip
                int mid = input.readInt();
                int len = input.readInt();
                Head head = new Head(ver, type, serializeType, mid, len);
                Block block = new Block();
                block.setHead(head);
                if (input.readableBytes() < len) {
                    last = block;
                    return;
                }
                block.setPayload(input.readBytes(len));
                out.on(block);
            }
        }
    }
}
