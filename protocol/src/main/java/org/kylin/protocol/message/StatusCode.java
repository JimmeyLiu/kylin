package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-7-28.
 */
public enum StatusCode {
    OK(200, "OK"),
    SERVICE_NOT_FOUND(4001, "Service Not Found"),
    METHOD_NOT_FOUND(4002, "Method Not Found"),
    TPS_LIMITED(5001, "TPS Limited"),
    WHITE_LIST_DENY(5002, "White List Deny"),
    //
    ;
    public int code;
    public String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
