
Kylin(麒麟)，一个跨语言的RPC框架

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


[更多文档](/docs)

1. [快速入门](/docs/quikstart.md)
2. [Kylin传输协议 KLTP](/docs/protocol.md)
3. [Kylin REST支持](/docs/restful.md)
4. [Kylin寻址](/docs/address.md)
5. [Kylin流量控制](/docs/traffic.md)
