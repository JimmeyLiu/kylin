package org.kylin.test;

import org.kylin.bootstrap.Kylin;
import org.kylin.test.service.TestServiceImpl;

/**
 * Created by jimmey on 15-6-23.
 */
public class Server {

    public static void main(String[] args) throws Exception {
        System.setProperty("kylin.appName", "TestServer");
        Kylin.provider(new TestServiceImpl());
    }

}
