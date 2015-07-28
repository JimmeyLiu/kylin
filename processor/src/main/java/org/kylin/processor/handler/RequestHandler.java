package org.kylin.processor.handler;

import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;

/**
 * Created by jimmey on 15-7-28.
 */
public interface RequestHandler {

    void handle(Request request, Response response) throws Exception;

}
