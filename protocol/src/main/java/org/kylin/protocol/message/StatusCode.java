package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-7-28.
 */
public enum StatusCode {
    TRYING(0, "Trying"),
    OK(200, "OK"),
    SERVER_ERROR(500, "Server Error"),
    SERVICE_NOT_FOUND(4041, "Service Not Found"),
    METHOD_NOT_FOUND(4042, "Method Not Found"),
    TPS_LIMITED_DENY(5001, "TPS Limited"),
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
