package org.kylin.test.service;

/**
 * Created by jimmey on 15-6-23.
 */
public interface TestService {

    public String say(String name,int age);

    public String hello(String name);

    public ResultModel hello(TestModel testModel);

}
