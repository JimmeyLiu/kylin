# Kylin Transport Protocol

Kylin设计之初就是为了跨语言，主要的实现方式为

1. 使用REST HTTP
2. 采用自定义传输协议，业务数据采用跨语言的序列化方式，如JSON、MessagePack、PB（暂未支持，可以自行扩展）

