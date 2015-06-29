package org.kylin.protocol.processor;

import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;

/**
 * Created by jimmey on 15-6-23.
 */
public interface RPCProcessor {

    void process(Request request, Callback callback);

    public interface Callback {
        void on(Response response);
    }
}
