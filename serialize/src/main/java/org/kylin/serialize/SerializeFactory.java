package org.kylin.serialize;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by jimmey on 15-6-22.
 */
public class SerializeFactory {
    private static Map<Integer, Serialize> map = new HashMap<Integer, Serialize>();

    static {
        ServiceLoader<Serialize> loader = ServiceLoader.load(Serialize.class);
        Iterator<Serialize> it = loader.iterator();
        while (it.hasNext()) {
            Serialize serialize = it.next();
            if (map.containsKey(serialize.type())) {
                throw new RuntimeException("duplicate serialize type " + serialize.type());
            }
            map.put(serialize.type(), serialize);
        }
    }

    public static Serialize get(int type) {
        return map.get(type);
    }

    public static byte[] serialize(int type, Object object) throws SerializeException {
        Serialize serialize = get(type);
        if (serialize == null) {
            throw new SerializeException("un support serialize type " + type);
        }
        return serialize.serialize(object);
    }

    public static <T> T deserialize(int type, byte[] in, Class<T> clazz) throws SerializeException {
        Serialize serialize = get(type);
        if (serialize == null) {
            throw new SerializeException("un support serialize type " + type);
        }
        return serialize.deserialize(in, clazz);
    }
}
