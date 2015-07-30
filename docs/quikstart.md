# Kylin快速入门


## 初始化
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


## 使用@Provider发布服务

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


## 使用@Consumer注入远程服务

```
@Component
public class Client {

    @Consumer
    TestService testService;

    ...
}
```


## 运行
```
public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Thread.sleep(1000);
        Client client = (Client) context.getBean("client");
        client.invoke();
    }

```