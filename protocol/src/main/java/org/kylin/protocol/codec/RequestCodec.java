package org.kylin.protocol.codec;

import org.kylin.common.util.ReflectUtils;
import org.kylin.common.util.StringUtils;
import org.kylin.protocol.message.Request;
import org.kylin.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.Map;

/**
 * Created by jimmey on 15-6-23.
 */
public class RequestCodec extends MessageCodec<Request> {
    @Override
    protected byte[] encodePayload(Request message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] serviceBytes = SerializeFactory.serialize(message.getSerializeType(), message.getServiceKey());
        writeBytesLength(out, serviceBytes);
        byte[] methodBytes = SerializeFactory.serialize(message.getSerializeType(), message.getMethod());
        writeBytesLength(out, methodBytes);

        byte[] types;
        if (message.getArgTypes() != null && message.getArgTypes().length > 0) {
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
        byte[] ctxBytes = EMPTY;
        Map<String, String> ctx = message.getContext();
        if (ctx != null) {
            ctxBytes = SerializeFactory.serialize(message.getSerializeType(), ctx);
        }
        writeBytesLength(out, ctxBytes);

        return out.toByteArray();
    }

    @Override
    protected Request instance(int serializeType) {
        return new Request(serializeType);
    }

    static final String[] EMPTY_TYPES = new String[0];
    static final Object[] EMPTY_ARGS = new Object[0];

    @SuppressWarnings("all")
    @Override
    protected void decodePayload(Request request, byte[] payload) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(payload);
        DataInputStream data = new DataInputStream(in);
        int serializeType = request.getSerializeType();
        String service = SerializeFactory.deserialize(serializeType, readLengthBytes(data), String.class);
        String method = SerializeFactory.deserialize(serializeType, readLengthBytes(data), String.class);

        request.setServiceKey(service);
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
        } else {
            request.setArgTypes(EMPTY_TYPES);
            request.setArgs(EMPTY_ARGS);
        }

        byte[] ctxBytes = readLengthBytes(data);
        if (ctxBytes.length > 0) {
            Map<String, String> ctx = SerializeFactory.deserialize(serializeType, ctxBytes, Map.class);
            request.setContext(ctx);
        }
    }
}
