package org.kylin.protocol.codec;

/**
 * Created by jimmey on 15-6-22.
 */
public class Head {

    int version;
    int type;
    int serializeType;
    int mid;
    int length;

    public Head(int version, int type, int serializeType, int mid, int length) {
        this.version = version;
        this.type = type;
        this.serializeType = serializeType;
        this.mid = mid;
        this.length = length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(int serializeType) {
        this.serializeType = serializeType;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
