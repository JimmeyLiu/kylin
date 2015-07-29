package org.kylin.processor.service;

/**
 * Created by jimmey on 15-7-29.
 * 对于服务端返回的非OK状态码，统一使用KylinException抛上去。
 * <p/>
 * 客户端可以根据code做对应的逻辑处理
 */
public class KylinException extends RuntimeException {

    private int code;

    public KylinException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
