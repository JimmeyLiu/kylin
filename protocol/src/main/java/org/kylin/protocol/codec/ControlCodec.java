package org.kylin.protocol.codec;

import org.kylin.protocol.message.Control;

/**
 * Created by jimmey on 15-7-28.
 */
public class ControlCodec extends MessageCodec<Control> {

    @Override
    protected byte[] encodePayload(Control message) throws Exception {
        return new byte[0];
    }

    @Override
    protected Control instance(int serializeType) {
        return new Control();
    }

    @Override
    protected void decodePayload(Control control, byte[] payload) throws Exception {

    }
}
