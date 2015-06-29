package org.kylin.protocol.codec;

import org.kylin.protocol.message.Response;
import org.kylin.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

/**
 * Created by jimmey on 15-6-26.
 */
public class ResponseCodec extends MessageCodec<Response> {
    @Override
    protected byte[] encodePayload(Response message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeInt(out, message.getStatus());
        byte[] resultBytes = SerializeFactory.serialize(message.getSerializeType(), message.getResult());
        writeBytesLength(out, resultBytes);

        byte[] bytes = EMPTY;
        if (message.getException() != null) {
            bytes = SerializeFactory.serialize(message.getSerializeType(), message.getException());
        }
        writeBytesLength(out, bytes);

        return out.toByteArray();
    }

    @Override
    protected Response instance(int serializeType) {
        return new Response(serializeType);
    }

    @Override
    protected void decodePayload(Response response, byte[] payload) throws Exception {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload));
        int status = data.readInt();
        byte[] resultBytes = readLengthBytes(data);

        response.setStatus(status);
        response.setResultBytes(resultBytes);

        byte[] exBytes = readLengthBytes(data);
        if (exBytes.length > 0) {
            response.setException(SerializeFactory.deserialize(response.getSerializeType(), exBytes, String.class));
        }
    }
}
