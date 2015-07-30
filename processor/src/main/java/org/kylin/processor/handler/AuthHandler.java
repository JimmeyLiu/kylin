package org.kylin.processor.handler;

import org.kylin.common.util.RequestCtxUtil;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.protocol.message.StatusCode;

/**
 * Created by jimmey on 15-7-30.
 */
public class AuthHandler implements RequestHandler {
    @Override
    public void handle(Request request, Response response) throws Exception {
        if (RequestCtxUtil.getClientAppName() == null) {
            response.setStatus(StatusCode.CLIENT_APP_REQUIRED);
            return;
        }
        //如果有服务端的token验证，，也在这里实现
    }
}
