package org.kylin.transport;

import org.kylin.common.guava.concurrent.AbstractFuture;
import org.kylin.common.log.RpcLogger;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by jimmey on 15-6-25.
 */
public class TransportFuture extends AbstractFuture<Response> {

    private Request request;

    private TransportFuture(Request request) {
        this.request = request;
    }

    public static TransportFuture create(Request request) {
        return new TransportFuture(request);
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        try {
            return super.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            RpcLogger.getLogger().error("request timeout {}", request.getTimeout());
            Response response = new Response(request.getSerializeType());
            response.setException("request timeout");
            return response;
        }
    }

    @Override
    public boolean set(Response value) {
        return super.set(value);
    }

    public Request getRequest() {
        return request;
    }
}
