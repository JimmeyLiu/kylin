package org.kylin.processor.handler;

import org.kylin.common.util.ReflectUtils;
import org.kylin.processor.service.ServiceFactory;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;

import java.lang.reflect.Method;

/**
 * Created by jimmey on 15-7-28.
 */
public class InvokeHandler implements RequestHandler {
    @Override
    public void handle(Request request, Response response) throws Exception {
        Object instance = ServiceFactory.getLocalService(request.getServiceKey());
        if (instance == null) {
            response.setStatus(StatusCode.SERVICE_NOT_FOUND);
            response.setException("service not found");
            return;
        }

        Method method = null;
        try {
            method = ReflectUtils.findMethodBySignature(instance.getClass(), request.getMethod(), request.getArgTypes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (method == null) {
            response.setStatus(StatusCode.METHOD_NOT_FOUND);
            response.setException("method not found");
            return;
        }

        Object result = method.invoke(instance, request.getArgs());
        response.setResult(result);
        response.setStatus(StatusCode.OK);
    }
}
