package org.kylin.protocol.codec;

import org.kylin.common.util.ReflectUtils;
import org.kylin.common.util.StringUtils;
import org.kylin.protocol.message.Request;
import org.kylin.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

/**
 * Created by jimmey on 15-6-23.
 */
public class RequestCodec extends MessageCodec<Request> {
    @Override
    protected byte[] encodePayload(Request message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] serviceBytes = SerializeFactory.serialize(message.getSerializeType(), message.getService());
        writeBytesLength(out, serviceBytes);
        byte[] methodBytes = SerializeFactory.serialize(message.getSerializeType(), message.getMethod());
        writeBytesLength(out, methodBytes);

        byte[] types;
        if (message.getArgTypes() != null) {
            types = SerializeFactory.serialize(message.getSerializeType(), StringUtils.join(message.getArgTypes(), ','));
        } else {
            types = EMPTY;
        }
        writeBytesLength(out, types);
        if (types.length > 0) {
            for (int i = 0; i < message.getArgs().length; i++) {
                byte[] argi = SerializeFactory.serialize(message.getSerializeType(), message.getArgs()[i]);
                writeBytesLength(out, argi);
            }
        }

        return out.toByteArray();
    }

    @Override
    protected Request instance(int serializeType) {
        return new Request(serializeType);
    }

    @Override
    protected void decodePayload(Request request, byte[] payload) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(payload);
        DataInputStream data = new DataInputStream(in);
        int serializeType = request.getSerializeType();
        String service = SerializeFactory.deserialize(serializeType, readLengthBytes(data), String.class);
        String method = SerializeFactory.deserialize(serializeType, readLengthBytes(data), String.class);

        request.setService(service);
        request.setMethod(method);

        byte[] argTypeBytes = readLengthBytes(data);
        if (argTypeBytes.length > 0) {
            String argTypesString = SerializeFactory.deserialize(serializeType, argTypeBytes, String.class);
            String[] argTypes = StringUtils.split(argTypesString, ',');
            Object[] args = new Object[argTypes.length];
            for (int i = 0; i < argTypes.length; i++) {
                args[i] = SerializeFactory.deserialize(serializeType, readLengthBytes(data), ReflectUtils.desc2Class(argTypes[i]));
            }
            request.setArgTypes(argTypes);
            request.setArgs(args);
        }


    }
}
