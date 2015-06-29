package org.kylin.serialize;

import java.io.*;

/**
 * Created by jimmey on 15-6-22.
 */
public class JavaSerialize implements Serialize {
    @Override
    public int type() {
        return 2;
    }

    @Override
    public byte[] serialize(Object in) throws SerializeException {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(byteArray);
            output.writeObject(in);
            output.flush();
            output.close();
            return byteArray.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }

    @SuppressWarnings("all")
    @Override
    public <T> T deserialize(byte[] in, Class<T> classType) throws SerializeException {
        try {
            ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(in));
            Object resultObject = objectIn.readObject();
            objectIn.close();
            return (T) resultObject;
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }
}
