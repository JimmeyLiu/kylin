package org.kylin.protocol.codec;

import org.kylin.protocol.message.Message;
import org.kylin.protocol.message.Request;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by jimmey on 15-6-23.
 */
public abstract class MessageCodec<T extends Message> {
    protected final byte[] EMPTY = new byte[0];
    static MessageCodec request;
    static MessageCodec response;

    static {
        request = new RequestCodec();
        response = new ResponseCodec();
    }

    public static void main(String[] args) throws Exception {
        Request request = new Request(1);
        request.setMethod("test");
        request.setService("fdsafdsa");
        request.setMid(123);
        MessageCodec codec = getCodec(Message.MessageType.REQUEST.ordinal());
        Block block = codec.encode(request);
        System.out.println(block);

        Message message = codec.decode(block);
        System.out.println(message);
    }

    public static MessageCodec getCodec(int messageType) throws Exception {
        Message.MessageType type = Message.MessageType.parse(messageType);
        switch (type) {
            case REQUEST:
                return request;
            case RESPONSE:
                return response;
            default:
                throw new Exception("un support type");
        }
    }


    public Block encode(T message) throws Exception {
        byte[] payload = encodePayload(message);
        Head head = new Head(1, message.getType().ordinal(), message.getSerializeType(), message.getMid(), payload.length);
        Block block = new Block();
        block.setHead(head);
        block.setPayload(payload);
        return block;
    }

    public T decode(Block block) throws Exception {
        T t = instance(block.getHead().getSerializeType());
        t.setMid(block.getHead().getMid());
        decodePayload(t, block.getPayload());
        return t;
    }

    protected abstract byte[] encodePayload(T message) throws Exception;

    protected abstract T instance(int serializeType);

    protected abstract void decodePayload(T t, byte[] payload) throws Exception;

    protected void writeBytesLength(ByteArrayOutputStream out, byte[] bytes) throws IOException {
        int v = bytes.length;
        writeInt(out, v);
        out.write(bytes);
    }

    protected void writeInt(ByteArrayOutputStream out, int v) {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    protected byte[] readLengthBytes(DataInputStream data) throws IOException {
        int len = data.readInt();
        if (len > 0) {
            byte[] bytes = new byte[len];
            data.read(bytes);
            return bytes;
        }
        return EMPTY;
    }
}
