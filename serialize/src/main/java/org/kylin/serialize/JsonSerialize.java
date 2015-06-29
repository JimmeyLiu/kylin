package org.kylin.serialize;

import com.alibaba.fastjson.JSON;

/**
 * Created by jimmey on 15-6-22.
 */
public class JsonSerialize implements Serialize {
    @Override
    public int type() {
        return 0;
    }

    @Override
    public byte[] serialize(Object in) throws SerializeException {
        try {
            return JSON.toJSONBytes(in);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] in, Class<T> classType) throws SerializeException {
        try {
            return JSON.parseObject(in, classType);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }
}
