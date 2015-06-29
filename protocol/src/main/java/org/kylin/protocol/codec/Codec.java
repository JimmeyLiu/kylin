package org.kylin.protocol.codec;

/**
 * Created by jimmey on 15-6-21.
 */
public interface Codec {

    void encode(Block block, Output output);

    void decode(Input input, Callback<Block> callback);

    public interface Callback<T> {
        void on(T t);
    }

    public interface Input {
        byte read();

        int readInt();

        long readLong();

        byte[] readBytes(int len);

        int readableBytes();
    }

    public interface Output {
        void write(byte b);

        void writeInt(int i);

        void writeLong(long l);

        void writeBytes(byte[] bytes);

    }

}
