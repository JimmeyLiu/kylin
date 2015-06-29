package org.kylin.serialize;

/**
 * Created by jimmey on 15-6-22.
 */
public interface Serialize {

    int type();

    byte[] serialize(Object in) throws SerializeException;

    <T> T deserialize(byte[] in, Class<T> classType) throws SerializeException;

}
