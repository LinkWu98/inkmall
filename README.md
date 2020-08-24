电商项目，基于分布式微服务架构。

一、 Spring Cloud 组件:
1. Nacos(注册、配置中心)
2. Gateway(网关)
3. Open Feign(http远程调用)
4. Sentinel + Zipkin(熔断降级 + 链路追踪)
...

二、中间件
1. RabbitMQ(服务间通信、削峰)
2. Redis(基于内存的非关系型数据库，读写效率高，分担数据库压力)
3. Elastic Search(基于倒排索引法的全文搜索引擎)
...

三、业务功能模块
1. index
2. search
3. item
4. cart
5. order
6. pms, oms, wms, sms
7. auth(sso-jwt)