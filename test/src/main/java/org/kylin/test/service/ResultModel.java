package org.kylin.test.service;

import org.msgpack.annotation.Message;

/**
 * Created by jimmey on 15-6-24.
 */
@Message
public class ResultModel {

    String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
