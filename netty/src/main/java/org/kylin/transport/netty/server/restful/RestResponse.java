package org.kylin.transport.netty.server.restful;

/**
 * Created by jimmey on 15-7-29.
 */
public class RestResponse {

    int code;
    String exception;
    Object result;

    public RestResponse() {
    }

    public RestResponse(int code, String exception, Object result) {
        this.code = code;
        this.exception = exception;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
