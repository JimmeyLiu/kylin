package org.kylin.processor.handler;

import org.kylin.processor.handler.tps.TpsHandler;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.processor.RPCProcessor;

/**
 * Created by jimmey on 15-6-23.
 */
public class RPCProcessorImpl implements RPCProcessor {

    RequestHandler[] handlers;

    public RPCProcessorImpl() {
        handlers = new RequestHandler[]{
                new TpsHandler(),//tps control
                new InvokeHandler()//real invoke
        };
    }

    @Override
    public void process(Request request, Callback callback) {
        Response response = new Response(request.getSerializeType());
        response.setMid(request.getMid());
        response.setStatus(0);
        long start = System.currentTimeMillis();
        try {
            for (RequestHandler handler : handlers) {
                handler.handle(request, response);
                if (response.getStatus() > 0) {
                    break;
                }
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.setException(e.getMessage());
        } finally {
            callback.on(response);
            long rt = System.currentTimeMillis() - start;
        }
    }

}
