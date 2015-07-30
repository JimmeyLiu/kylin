# Kylin Transport Protocol

Kylin设计之初就是为了跨语言，主要的实现方式为

1. 使用REST HTTP
2. 采用自定义传输协议，业务数据采用跨语言的序列化方式，如JSON、MessagePack、PB（暂未支持，可以自行扩展）

## 消息制

Kylin的请求响应采用消息制，即一个 Request (protocol/src/main/java/org/kylin/protocol/message/Request.java)得到一个Response(protocol/src/main/java/org/kylin/protocol/message/Response.java)。


