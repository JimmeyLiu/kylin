# Kylin Transport Protocol

Kylin设计之初就是为了跨语言，主要的实现方式为

1. 使用REST HTTP
2. 采用自定义传输协议，业务数据采用跨语言的序列化方式，如JSON、MessagePack、PB（暂未支持，可以自行扩展）

## 消息制

Kylin的请求响应采用消息制，即一个 [Request](/protocol/src/main/java/org/kylin/protocol/message/Request.java)得到一个[Response](/protocol/src/main/java/org/kylin/protocol/message/Response.java)。

### Message基础字段
1. int mid: 消息ID，用于关联请求和响应
2. MessageType type: 消息类型，有 REQUEST, RESPONSE, CONTROL
3. int serializeType: 消息的序列化方式

### Request 字段

1. String serviceKey: 请求对应的serviceKey
2. String method: 请求的方法
3. String[] argTypes: 方法参数签名数组
4. Object[] args: 参数数组
5. int timeout = 1000: 请求超时时间，单位为ms，默认为1000ms
6. Map<String, String> context: 请求上下文信息，为了简单，只支持String键值对。主要用于传递请求过程中一些额外上下文信息，如ClientAppName、TraceId等等

### Response字段

1. int status: 响应状态码，参考[StatusCode](/protocol/src/main/java/org/kylin/protocol/message/StatusCode.java)
2. Object result: 响应的结果数据，服务端设置响应结果时使用
3. byte[] resultBytes: 结果数据序列化后的byte数组，还没有反序列化。拿到真正的结果需要传入返回的类型信息得到对应的对象
4. String exception: 如果处理失败（客户端或服务端都有可能失败），则这里设置内容信息


## 消息传输协议

### 通用协议头

Kylin传输协议头为16字节，头格式如下

```
+----+-+-+-+-+----+----+---...---+
|KLYN|V|T|S|R|MID |LEN | PAYLOAD |
+----+-+-+-+-+----+----+---...---+
```

