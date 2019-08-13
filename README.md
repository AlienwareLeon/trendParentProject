# 基于spingcloud的量化交易系统

实现了。。。。。。

项目拓扑图：

----------

# 必要条件

[JKD1.8](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
hutool
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

----------


## 2. third-part-index-data-project 模拟第三方数据

启动类： ThirdPartIndexDataApplication ： 使用端口：8090



### 注解

 @EnableEurekaClient 表示注册为微服务


----------

## 3. index-gather-store-service 采集存储第三方数据

**Index启动类：** IndexGatherStoreApplication： 

**redis:**使用端口：6379

Index指数代码使用端口：8001  

    
    @Bean
    RestTemplate restTemplate() {
    return new RestTemplate();
    }
    声明后IndexService 才能够使用 RestTemplate


@EnableCaching 表示启动缓存

redisPort，用于判断 redis 服务器是否启动


### 3.1.以下为有关指数代码（Index）服务
**指数代码pojo：Index**

code 指数代码

name 指数代码名称

**IndexService和Index的断路器及redis：**

@EnableHystrix 启动断路器

在获取第三方指数代码fetch_indexes_from_third_part 添加@HystrixCommand(fallbackMethod = "third_part_not_connected")，表示如果获取第三数据失败，自动调用third_part_not_connected并且返回

@CacheConfig(cacheNames="indexes"),redis缓存的名称是 indexes,保存到 redis 以 indexes 命名

在fetch_indexes_from_third_part 方法上增加：@Cacheable(key="'all_codes'") 表示保存到 redis 用的 key 使用 all_codes


**IndexService的Index的redis刷新服务：**

**util包下的SpringContextUtil工具类用于IndexService的Index的redis刷新服务中获取IndexService：**

> 1.如果一开始忘记启动第三方了，那么redis里保存的就会是断路信息
> 
> 2.如果第三方刷新了， redis 也没有办法刷新

刷新redis数据 fresh()
1. 先运行 fetch_indexes_from_third_part 来获取第三方数据 
2. 删除redis数据 remove()
3. 保存到redis数据 store()



**IndexController**

    @RestCotroller返回Json
    
    //  http://127.0.0.1:8001/freshCodes
    
    //  http://127.0.0.1:8001/getCodes
    
    //  http://127.0.0.1:8001/removeCodes



**定时器**



### 3.2 Redis的配置类RedisCacheConfig


### 3.3 以下为有关指数数据（IndexData）服务
**pojo:IndexData**

data: 时间

closePoint : 收盘价

**IndexDataService和IndexData的断路器及redis：**

对应IndexService，获取的是指数数据IndexData

指数数据存放的key 是 indexData-code-000300 


**IndexDataController**

     
//  http://127.0.0.1:8001/freshIndexData/000300

//  http://127.0.0.1:8001/getIndexData/000300

//  http://127.0.0.1:8001/removeIndexData/000300

对应IndexController 访问时候需要加指数代码的编码，例如：

    @GetMapping("/freshIndexData/{code}")
    public String fresh(@PathVariable("code") String code) throws Exception {
    indexDataService.fresh(code);
    return "fresh index data successfully";
    }


### 3.4 以下为采集和存储微服务的定时器（Quartz）：

**job--IndexDataSyncJob**

任务类，同时刷新指数代码和指数数据

**config--QuartzConfiguration**

定时器配置，每隔一分钟执行一次


----------

## 4. index-codes-service

### 专门用index-codes-service微服务来单纯地提供指数代码

为了使用集群，指数代码微服务会有多个， 而采集和存储服务并非不能做集群，但是它里面有定时器，如果也做成集群，就会有多个定时器同时工作，一起向第三方获取数据，一起把数据保存到 redis里。 这样不仅是额外的开销，也埋下了出现数据冲突的风险


**启动类IndexCodesApplication**

端口：8011/8012/8013

默认使用8011

**Index:pojo：指数类**

**cofig--IpConfiguration**

获取当前端口号，不同index-codes-service微服务使用不同的端口号，通过获取并打印出当前是哪个端口

**config--RedisCacheConfig**

Redis的配置类


**IndexService**

服务类，直接从Reid获取数据，如果没有数据，返回“无效指数代码”

**IndexController**

返回代码集合，并通过 IpConfiguration 获取当前接口并打印

@CrossOrigin 表示允许跨域，回测视图是另一个端口号的，访问这个服务是属于跨域了


----------

## 5. index-data-service

### 单独提供数据服务


提供端口：8021/8022/8023

**cofig--IpConfiguration**

同代码指数的微服务


**config--RedisCacheConfig**

Redis的配置类，同代码指数的微服务

**Index:pojo：指数数据实体类**


**IndexSDataervice**

服务类，直接从Reid获取数据

**IndexDataController**

提供 web 接口，访问地址是
 
http://127.0.0.1:8021/data/000300


## 6.index-zuul-service网关

**pom:**

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
 

**application.yml**

    eureka:
      client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    spring:
      application:
    name: index-zuul-service
      zipkin:
    base-url: http://localhost:9411   
    zuul:
      routes:
    api-a:
      path: /api-codes/**
      serviceId: INDEX-CODES-SERVICE

所有的访问 /api-codes/ 的请求，都会自动转到 INDEX-CODES-SERVICE 去。 而 INDEX-CODES-SERVICE 有3个，就会在这3个之间来回切换。

**IndexZuulServiceApplication**

启动类，端口号：8031

@EnableZuulProxy 表示网关


----------

## 7. trend-trading-backtest-service

趋势交易回测微服务为趋势交易回测视图提供数据

**pom :**

        <!-- feign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

启动 Feign 方式访问其他微服务


**application.yml**

    feign.hystrix.enabled: true

用于开启 feign 模式的断路器


**pojo:IndexData**

**client--IndexDataClient**


