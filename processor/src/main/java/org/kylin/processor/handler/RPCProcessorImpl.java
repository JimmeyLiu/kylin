package org.kylin.processor.handler;

import org.kylin.common.log.RpcLogger;
import org.kylin.common.util.RequestCtxUtil;
import org.kylin.processor.handler.traffic.TrafficHandler;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;
import org.kylin.protocol.processor.RPCProcessor;
import org.slf4j.Logger;

/**
 * Created by jimmey on 15-6-23.
 */
public class RPCProcessorImpl implements RPCProcessor {

    RequestHandler[] handlers;
    Logger logger = RpcLogger.getLogger();

    public RPCProcessorImpl() {
        handlers = new RequestHandler[]{
                new TrafficHandler(),//tps control
                new InvokeHandler()//real invoke
        };
    }

    @Override
    public void process(Request request, Callback callback) {
        Response response = new Response(request.getSerializeType());
        response.setMid(request.getMid());
        response.setStatus(StatusCode.TRYING);
        try {
            for (RequestHandler handler : handlers) {
                handler.handle(request, response);
                if (response.getStatus() != StatusCode.TRYING.code) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Handle %s.%s Error", request.getServiceKey(), request.getMethod()), e);
            response.setStatus(StatusCode.SERVER_ERROR);
            response.setException(e.getMessage());
        } finally {
            callback.on(response);
        }
    }

}
