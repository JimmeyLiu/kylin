package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-6-20.
 */
public abstract class Message {

    public enum MessageType {
        REQUEST, RESPONSE, CONTROL;

        public static MessageType parse(int type) {
            return values()[type];
        }
    }

    int mid;
    MessageType type;
    int serializeType;

    protected Message(MessageType type, int serializeType) {
        this.type = type;
        this.serializeType = serializeType;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public MessageType getType() {
        return type;
    }

    public int getSerializeType() {
        return serializeType;
    }

}
