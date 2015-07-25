package org.kylin.test.service;

import org.kylin.spring.Provider;
import org.springframework.stereotype.Component;

/**
 * Created by jimmey on 15-6-23.
 */
@Provider(version = "")
@Component
public class TestServiceImpl implements TestService {
    @Override
    public String say(String name, int age) {
        return "welcome " + name + " age " + age;
    }

    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @Override
    public ResultModel hello(TestModel testModel) {
        ResultModel resultModel = new ResultModel();
        resultModel.setResult("hello " + testModel.getName() + " age " + testModel.getAge() + " time " + testModel.getTime());
        return resultModel;
    }
}
