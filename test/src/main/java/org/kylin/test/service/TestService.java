package org.kylin.test.service;

/**
 * Created by jimmey on 15-6-23.
 */
public interface TestService extends BaseService {

    public ResultModel say();

    public long say(int i, long j);

    public String say(String name, int age);

    public String hello(String name);

    public ResultModel hello(TestModel testModel);

}
