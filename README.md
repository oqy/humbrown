humbrown
===========

以spring+mybatis为框架的jee开发基础包，提供用于开发业务对象、dao、service、缓存、安全等业务的基类对象和相关工具类。

项目选型：

MVC框架：Spring mvc，使用Thymeleaf渲染view

应用层：Spring 3

持久层：Mybatis 3

缓存：Spring Cache，提供redis缓存实现

安全框架：Shiro，提供基于数据库的Realm及Principal基类，可采用框架提供的Ehcache缓存方案，或使用工程提供的工具类实现以redis为存储载体的Session Cluster

搜索引擎：ElasticSearch，提供工具类以实现index和query基础操作