package org.kylin.test;

import org.kylin.processor.service.KylinException;
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
        System.out.println(testService.hello(testModel).getResult());

        for (int i = 10; i < 2000; i++) {
            try {
                testModel.setAge(i);
                System.out.println(testService.hello(testModel).getResult());
            } catch (KylinException e) {
                System.out.println(e.getCode() + " " + e.getMessage());
            }
            Thread.sleep(500);
        }

    }

    public static void main(String[] args) throws Exception {
        System.setProperty("kylin.appName", "TestAppName1");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Thread.sleep(1000);
        Client client = (Client) context.getBean("client");
        client.invoke();
    }

}
