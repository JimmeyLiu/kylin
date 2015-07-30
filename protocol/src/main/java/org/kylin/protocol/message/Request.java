package org.kylin.protocol.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmey on 15-6-20.
 */
public class Request extends Message {
    String serviceKey;
    String method;
    String[] argTypes;
    Object[] args;
    int timeout = 1000;
    Map<String, String> context;

    public Request(int serializeType) {
        super(MessageType.REQUEST, serializeType);
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(String[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public void putContext(String key, String value) {
        if (context == null) {
            context = new HashMap<String, String>();
        }
        context.put(key, value);
    }
}
