
kylin(麒麟)，一个跨语言的RPC框架

如何跨语言

1. 底层传输协议使用自定义二进制协议，不同语言实现传输协议
2. 业务数据、模型传输序列化采用跨语言方案，如json、MessagePack、pb等等


主要模块

1. protocol：底层传输协议
2. serialize：业务数据序列化模块
3. transport：网络通讯传输模块，其中netty为transport基于netty的具体实现
4. common：公共类、方法
5. address：服务注册、发现模块；如基于config系统、zk
6. spring：和spring容器整合使用
7. processor：RPC初始化入口
8. test：测试工程模块
9. config：配置接口
10. restful：服务以HTTP REST方式提供出去


使用方法

1. 初始化
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"

       xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd">
    <bean class="org.kylin.bootstrap.SpringBootstrap"/>
    <context:component-scan base-package="org.kylin.test"/>
</beans>
```
2. 使用@Provider发布服务
```
@Provider
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
```
3. 使用@Consumer注入远程服务
```
@Component
public class Client {

    @Consumer
    TestService testService;

    ...
}
```
4. 运行
```
public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Thread.sleep(1000);
        Client client = (Client) context.getBean("client");
        client.invoke();
    }

```

