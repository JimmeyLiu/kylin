package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-6-20.
 */
public abstract class Message {

    public enum MessageType {
        HELLO_REQUEST, HELLO_RESPONSE, REQUEST, RESPONSE;

        public static MessageType parse(int type) {
            return values()[type];
        }
    }

    long mid;
    MessageType type;
    int serializeType;

    protected Message(MessageType type, int serializeType) {
        this.type = type;
        this.serializeType = serializeType;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public MessageType getType() {
        return type;
    }

    public int getSerializeType() {
        return serializeType;
    }

}
