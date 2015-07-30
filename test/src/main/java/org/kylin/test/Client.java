package org.kylin.test;

import org.kylin.bootstrap.annotation.Consumer;
import org.kylin.test.service.TestModel;
import org.kylin.test.service.TestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
        for (int i = 0; i < 600; i++) {
            long start = System.currentTimeMillis();
            for (int j = 0; j < 1000; j++) {
                System.out.println(testService.hello("fdsafdsa"));
            }
            System.out.println("used " + (System.currentTimeMillis() - start));
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("kylin.appName", "TestClient2");
        System.setProperty("kylin.online", "false");

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("client.xml");
        Client client = (Client) context.getBean("client");
        client.invoke();

    }

}
