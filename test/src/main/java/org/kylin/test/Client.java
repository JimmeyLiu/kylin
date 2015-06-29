package org.kylin.test;

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

    public void invoke() {
        TestModel testModel = new TestModel();
        testModel.setName("fdsa");
        testModel.setAge(1234324);
        testModel.setTime(1111111);
        System.out.println(testService.hello(testModel).getResult());
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Thread.sleep(1000);
        Client client = (Client) context.getBean("client");
        client.invoke();
    }

}
