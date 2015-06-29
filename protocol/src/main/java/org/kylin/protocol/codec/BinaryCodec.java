package org.kylin.protocol.codec;

/**
 * Created by jimmey on 15-6-22.
 */
public class BinaryCodec implements Codec {

    static final byte[] RESERVED = new byte[]{0, 0, 0};

    Block last = null;

    @Override
    public void encode(Block block, Output output) {
        output.write((byte) block.getHead().getVersion());
        output.write((byte) block.getHead().getType());
        output.write((byte) block.getHead().getSerializeType());
        output.writeBytes(RESERVED);
        output.writeLong(block.getHead().getMid());
        output.writeInt(block.getHead().getLength());
        if (block.getPayload() != null) {
            output.writeBytes(block.getPayload());
        }
    }

    @Override
    public void decode(Input input, Callback<Block> out) {
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
                int ver = input.read();
                int type = input.read();
                int serializeType = input.read();
                input.read();//skip
                input.read();//skip
                input.read();//skip
                long mid = input.readLong();
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
