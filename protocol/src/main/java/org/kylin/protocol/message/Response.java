package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-6-20.
 */
public class Response extends Message {

    int status;
    Object result;
    private byte[] resultBytes;
    String exception;

    public Response(int serializeType) {
        super(MessageType.RESPONSE, serializeType);
    }

    public static Response errorResponse(int serializeType, StatusCode statusCode) {
        Response response = new Response(serializeType);
        response.setStatus(statusCode);
        return response;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(StatusCode statusCode) {
        this.status = statusCode.code;
        if (statusCode != StatusCode.OK) {
            this.exception = statusCode.message;
        } else {
            this.exception = null;
        }
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public byte[] getResultBytes() {
        return resultBytes;
    }

    public void setResultBytes(byte[] resultBytes) {
        this.resultBytes = resultBytes;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
