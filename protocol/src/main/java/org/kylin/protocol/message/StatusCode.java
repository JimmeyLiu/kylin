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
    REST_BAD_REQUEST(7000, "Restful URI Error"),
    REST_PARAM_ERROR(7001, "Restful Param Error"),
    REST_ARGTYPE_ERROR(7001, "Restful ArgTypes Error"),
    //
    ;
    public int code;
    public String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
