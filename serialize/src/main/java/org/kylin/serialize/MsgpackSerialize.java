package org.kylin.serialize;

import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;

import java.util.Map;

/**
 * Created by jimmey on 15-6-22.
 */
public class MsgpackSerialize implements Serialize {
    MessagePack pack;

    @SuppressWarnings("unchecked")
    public MsgpackSerialize() {
        pack = new MessagePack();
        pack.register(Map.class, (Template) Templates.tMap(Templates.TString, Templates.TString));
    }

    @Override
    public int type() {
        return 1;
    }

    @Override
    public byte[] serialize(Object in) throws SerializeException {
        try {
            return pack.write(in);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] in, Class<T> classType) throws SerializeException {
        try {
            return pack.read(in, classType);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

}
