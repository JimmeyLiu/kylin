package org.kylin.test;

import org.kylin.common.util.RequestCtxUtil;
import org.kylin.spring.Consumer;
import org.kylin.test.service.TestModel;
import org.kylin.test.service.TestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by jimmey on 15-6-23.
 */
@Component
public class Client {

    @Consumer
    TestService testService;

    public void invoke() throws Exception {
        TestModel testModel = new TestModel();
        testModel.setName("的撒范德萨发");
        testModel.setAge(1234324);
        testModel.setTime(1111111);
        RequestCtxUtil.setTargetServer("10.125.48.99:18000?IDLE_TIMEOUT=60&CONNECT_TIMEOUT=1000&SERIALIZE=msgpack,json");
        for (int i = 0; i < 600; i++) {
            long start = System.currentTimeMillis();
            for (int j = 0; j < 1000; j++) {
                testService.say();
            }
            System.out.println("used " + (System.currentTimeMillis() - start));
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("kylin.appName", "TestClient2");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("client.xml");
        Thread.sleep(1000);
        Client client = (Client) context.getBean("client");
        client.invoke();
    }

}
