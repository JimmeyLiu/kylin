package org.kylin.bootstrap.bean;

/**
 * Created by jimmey on 15-7-17.
 */
public class ProviderBean {

    private String service;
    private String version;
    private Object target;

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

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
