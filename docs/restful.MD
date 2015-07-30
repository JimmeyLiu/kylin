
# Kylin REST HTTP支持

## 主要场景
Kylin支持REST HTTP调用服务，主要场景有

1. 简单验证服务是否可用
2. 非java应用通过REST方式调用服务


## 请求格式

```
POST /RPC/{serviceKey}/{method}[/{argTypesSign}]
```

格式说明：

1. 注意！！REST请求只支持HTTP POST
2. serviceKey为服务发布的key，如 org.kylin.test.service.TestService:1.0.0
3. method为调用服务的方法名，如 say
4. argTypesSign，参数类型签名，在无参方法时可以忽略；有参方法一定要有。用来准确定位到一个服务方法

如下请求报文截图

```
curl -i -H"Client-App:TestRest" -X POST "10.125.48.99:18000/RPC/org.kylin.test.service.TestService:1.0.0/say"
HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
Content-Length: 51
Connection: Keep-Alive

{"code":200,"result":{"result":"和会话会话f"}}
```



