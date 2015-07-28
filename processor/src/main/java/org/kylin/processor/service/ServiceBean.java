package org.kylin.processor.service;

import java.util.Map;

/**
 * Created by jimmey on 15-7-17.
 */
public class ServiceBean {


    private String service;
    private String version;
    private Object target;
    private Map<String, Integer> methodTimeout;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Integer> getMethodTimeout() {
        return methodTimeout;
    }

    public void setMethodTimeout(Map<String, Integer> methodTimeout) {
        this.methodTimeout = methodTimeout;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
