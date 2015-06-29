package org.kylin.protocol.codec;

/**
 * Created by jimmey on 15-6-21.
 */
public class Block {

    static final int HEAD_SIZE = 16;

    Head head;

    byte[] payload;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
