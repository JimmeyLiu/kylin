
# Kylin REST HTTP支持

## 主要场景
Kylin支持REST HTTP调用服务，主要场景有

1. 简单验证服务是否可用
2. 非java应用通过REST方式调用服务


## 请求格式

```
POST /RPC/{serviceKey}/{method}[/{argTypesSign}]
[args JSONArray]
```

格式说明：

1. 注意！！REST请求只支持HTTP POST
2. serviceKey为服务发布的key，如 org.kylin.test.service.TestService:1.0.0
3. method为调用服务的方法名，如 say
4. argTypesSign，参数类型签名，在无参方法时可以忽略；有参方法一定要有。用来准确定位到一个服务方法
5. args JSONArray，参数值使用JSONArray在body中传递

如下请求报文截图

```
curl -i -H"Client-App:TestRest" -X POST "10.125.48.99:18000/RPC/org.kylin.test.service.TestService:1.0.0/say"
HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
Content-Length: 51
Connection: Keep-Alive

{"code":200,"result":{"result":"和会话会话f"}}
```

### 参数说明

1. 所有请求必须给出Client-App，在Header中增加，如上 Client-App:TestRest
2. 如果需要支持trace，则也通过Header增加
3. 请求body为JSONArray，按照 argTypesSign类型顺序；如果参数个数不对会报错参数错误

## 响应说明

1. 在服务正常的情况下，所有的响应状态码都为 200 OK
2. 响应内容类型为 application/json ，编码为 UTF-8
3. 如果客户端支持KeepAlive，则服务端不会主动关闭连接
4. 响应内容为RestResponse，字段说
    1. code：Kylin定义的状态码，详见附录
    2. exception：如果code不为200，则这里显示错误信息
    3. result：如果code为200，这里为服务返回结果


# 附录

## StatusCode

```
TRYING(0, "Trying"),
ADDRESS_NOT_FOUND(100, "Address Not Found"),
CLIENT_SEND_ERROR(101, "Client Send Error"),
REQUEST_TIMEOUT(102, "Request Timeout"),
OK(200, "OK"),
CLIENT_APP_REQUIRED(301, "Client AppName Required"),
SERVER_ERROR(500, "Server Error"),
SERVICE_NOT_FOUND(401, "Service Not Found"),
METHOD_NOT_FOUND(402, "Method Not Found"),
TPS_LIMITED_DENY(501, "TPS Limited"),
WHITE_LIST_DENY(502, "White List Deny"),
REST_BAD_REQUEST(700, "Restful URI Error"),
REST_PARAM_ERROR(701, "Restful Param Error"),
REST_ARGTYPE_ERROR(702, "Restful ArgTypes Error"),
```
