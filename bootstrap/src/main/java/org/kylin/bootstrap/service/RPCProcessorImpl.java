package org.kylin.bootstrap.service;

import org.kylin.common.util.ReflectUtils;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.processor.RPCProcessor;

import java.lang.reflect.Method;

/**
 * Created by jimmey on 15-6-23.
 */
public class RPCProcessorImpl implements RPCProcessor {

    @Override
    public void process(Request request, Callback callback) {
        Response response = new Response(request.getSerializeType());
        response.setMid(request.getMid());
        try {
            Class<?> service = ReflectUtils.forName(request.getService());
            Object instance = ServiceFactory.getLocalService(service);
            if (instance == null) {
                response.setStatus(404);
                response.setException("service not found");
                callback.on(response);
                return;
            }

            Method method = null;
            try {
                method = ReflectUtils.findMethodBySignature(service, request.getMethod(), request.getArgTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (method == null) {
                response.setStatus(404);
                response.setException("method not found");
                callback.on(response);
                return;
            }

            Object result = method.invoke(instance, request.getArgs());
            response.setResult(result);
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(500);
            response.setException(e.getMessage());
        }
        callback.on(response);
    }

}
