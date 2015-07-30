package org.kylin.address;

import org.kylin.common.util.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmey on 15-7-16.
 */
public class Address {

    private int serializeType = 1;

    private URI uri;

    private int idleTimeout = 60;

    private int connectTimeout = 1000;

    public Address() {
    }

    /**
     * 10.1.0.1:10000?serialize=msgpack,json,java&CONNECT_TIMEOUT=200&IDLE_TIMEOUT=120
     *
     * @param address
     * @return
     */
    public static Address parse(String address) {
        Address tmp = new Address();
        int i = address.indexOf("?");
        if (i <= 0) {
            tmp.uri = URI.create("tcp://" + address);
        } else {
            tmp.uri = URI.create("tcp://" + address.substring(0, i));
            String[] v = address.substring(i + 1).toUpperCase().split("&");
            Map<String, String> map = new HashMap<String, String>(v.length);
            for (String s : v) {
                String[] p = s.split("=");
                if (p.length == 2) {
                    map.put(p[0], p[1]);
                }
            }
            String idle = map.remove("IDLE_TIMEOUT");
            if (idle != null && StringUtils.isNumeric(idle)) {
                tmp.idleTimeout = Integer.valueOf(idle);
            }
            if (tmp.idleTimeout < 10) {
                tmp.idleTimeout = 10;
            }

            String ct = map.remove("CONNECT_TIMEOUT");
            if (idle != null && StringUtils.isNumeric(ct)) {
                tmp.connectTimeout = Integer.valueOf(ct);
            }
            if (tmp.connectTimeout < 200) {
                tmp.connectTimeout = 200;
            }

            String serialize = map.remove("SERIALIZE");
            if (serialize != null) {
                String[] types = StringUtils.split(serialize, ',');
            }
            tmp.serializeType = 1;
        }
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;

        Address address = (Address) o;

        if (connectTimeout != address.connectTimeout) return false;
        if (idleTimeout != address.idleTimeout) return false;
        if (serializeType != address.serializeType) return false;
        if (uri != null ? !uri.equals(address.uri) : address.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serializeType;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + idleTimeout;
        result = 31 * result + connectTimeout;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(uri.getHost()).append(":").append(uri.getPort()).append("?");
        sb.append("IDLE_TIMEOUT=").append(idleTimeout).append("&");
        sb.append("CONNECT_TIMEOUT=").append(connectTimeout).append("&");
        sb.append("SERIALIZE=msgpack,json");
        return sb.toString();
    }

    public int getSerializeType() {
        return serializeType;
    }

    public URI getUri() {
        return uri;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
