package org.kylin.bootstrap.bean;

/**
 * Created by jimmey on 15-7-30.
 */
public class ConsumerBean {
    private String service;
    private String version;
    private String target;

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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
