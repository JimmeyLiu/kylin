package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-7-28.
 */
public enum StatusCode {
    TRYING(0, "Trying"),
    CLIENT_SEND_ERROR(100, "Client Send Error"),
    OK(200, "OK"),
    SERVER_ERROR(500, "Server Error"),
    SERVICE_NOT_FOUND(401, "Service Not Found"),
    METHOD_NOT_FOUND(402, "Method Not Found"),
    TPS_LIMITED_DENY(501, "TPS Limited"),
    WHITE_LIST_DENY(502, "White List Deny"),
    REST_BAD_REQUEST(700, "Restful URI Error"),
    REST_PARAM_ERROR(701, "Restful Param Error"),
    REST_ARGTYPE_ERROR(702, "Restful ArgTypes Error"),
    //
    ;
    public int code;
    public String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
