# 基于spingcloud的量化交易系统

实现了。。。。。。

项目拓扑图：

----------

#必要条件

[JKD1.8](https://www.oracle.com/technetwork/java/javase/downloads/index.html)

----------

# 技术栈

----------

## 前端：

**html, CSS, Javascript, JSON, AJAX, JQuery ,Bootstrap, Vue.js ，chartjs**

## 后端：
**spring,springmvc,springboot2.x**

## 中间件：
**redis**

## 开发工具：
**Intellij IDEA2017，Maven，Git**

## 分布式：
**SpringCloud(Finchley.RELEASE)**

## 数据来源本地的第三方数据(JSON)

third-part-index-data-project---resources

所有指数代码codes.json  closePoint收盘价

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


## 2. third-part-index-data-project 模拟第三方数据

启动类： ThirdPartIndexDataApplication ： 使用端口：8090



### 注解

 @EnableEurekaClient 表示注册为微服务


## 3. index-gather-store-service 采集存储第三方数据

### Index
**Index代码指数数据pojo**

**Index启动类：** IndexGatherStoreApplication： 

**redis:**使用端口：6379

Index代码指数使用端口：8001  
    
    @Bean
    RestTemplate restTemplate() {
    return new RestTemplate();
    }
    声明后IndexService 才能够使用 RestTemplate

@EnableCaching 表示启动缓存

redisPort，用于判断 redis 服务器是否启动


**IndexService和Index的断路器及redis：**

@EnableHystrix 启动断路器

在获取第三方指数代码数据fetch_indexes_from_third_part 添加@HystrixCommand(fallbackMethod = "third_part_not_connected")，表示如果获取第三数据失败，自动调用third_part_not_connected并且返回

@CacheConfig(cacheNames="indexes"),redis缓存的名称是 indexes,保存到 redis 以 indexes 命名

在fetch_indexes_from_third_part 方法上增加：@Cacheable(key="'all_codes'") 表示保存到 redis 用的 key 使用 all_codes


**刷新服务**

> 1.如果一开始忘记启动第三方了，那么redis里保存的就会是断路信息
> 2.如果第三方刷新了， redis 也没有办法刷新



**定时器**



### RedisCacheConfig 配置类RedisCacheConfig


### Index
**pojo**
**启动类**
**redis**
**Service和断路器及redis**
**定时器**

##