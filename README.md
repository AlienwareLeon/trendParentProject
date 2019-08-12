# 基于spingcloud的量化交易系统

实现了。。。。。。

项目拓扑图：

# 技术栈

----------

## 前端：

**html, CSS, Javascript, JSON, AJAX, JQuery ,Bootstrap, Vue.js ，chartjs**

## 后端：
**spring,springmvc,springboot2.x**

## 中间件：
**redis**

## 开发工具：
**Intellij IDEA，Maven，Git**

## 分布式：
**SpringCloud(Finchley.RELEASE)**

## 数据来源本地第三方数据(JSON)

third-part-index-data-project---resources

----------

# 模块开发

## 1. eureka-server服务注册中心

启动类 ： EurekaServerApplication：使用8761端口

### 注解

1. @SpringBootApplication 启动类
2. @EnableEurekaServer 注册中心服务器
3. 默认端口号是 8761
4. 进行端口号判断，如果这个端口已经被占用了，就给出提示信息。
5. 使用 SpringApplicationBuilder 进行启动


## 2. third-part-index-data-project 第三方数据

启动类： ThirdPartIndexDataApplication ： 使用8090端口



### 注解

 @EnableEurekaClient 表示注册为微服务







	