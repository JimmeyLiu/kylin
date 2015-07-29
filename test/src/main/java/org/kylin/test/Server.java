package org.kylin.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jimmey on 15-6-23.
 */
public class Server {

    public static void main(String[] args) throws Exception {
        System.setProperty("kylin.appName", "TestServer");
        new ClassPathXmlApplicationContext("server.xml");
    }

}
