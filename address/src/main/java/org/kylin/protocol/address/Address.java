package org.kylin.protocol.address;

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

    private int idleTimeout;

    private int connectTimeout = 1000;

    /**
     * 10.1.0.1:10000?serialize=msgpack,json,java&CONNECT_TIMEOUT=200&IDLE_TIMEOUT=120
     *
     * @param address
     */
    public Address(String address) {
        int i = address.indexOf("?");
        if (i <= 0) {
            this.uri = URI.create("tcp://" + address);
        } else {
            this.uri = URI.create("tcp://" + address.substring(0, i));
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
                idleTimeout = Integer.valueOf(idle);
            }
            if (idleTimeout < 10) {
                idleTimeout = 10;
            }

            String ct = map.remove("CONNECT_TIMEOUT");
            if (idle != null && StringUtils.isNumeric(ct)) {
                connectTimeout = Integer.valueOf(ct);
            }
            if (connectTimeout < 200) {
                connectTimeout = 200;
            }

            String serialize = map.remove("SERIALIZE");
            if (serialize != null) {
                String[] types = StringUtils.split(serialize, ',');
            }
            serializeType = 1;
        }

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
}
