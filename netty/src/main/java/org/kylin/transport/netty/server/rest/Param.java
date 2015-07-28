package org.kylin.transport.netty.server.rest;

/**
 * Created by jimmey on 15-7-28.
 */
public class Param {

    String[] argTypes;
    Object[] args;

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
}
