# 基于spingcloud的量化交易系统

1. 通过切换不同的指数，可以在曲线上看到指数收益以及趋势投资收益的可视化区别
2. 在收益一览可以看到数字话的趋势投资收益比较
3. 在交易统计可以看到总共多少次交易，盈亏比，平均盈利比率，亏损比率。
4. 在收益分布对比表可以看到看到不同年份的收益比较表
6. 在收益分布对比图可以看到不同年份的收益比较图
7. 在交易明细可以看到每一笔交易的开始结束时间，对应的收盘点以及盈亏情况

除此之外还可以调整参数观察不同参数情况下的趋势投资收益变化：

1. 调整均线
2. 调整买入阈值
3. 调整卖出阈值
4. 引入手续费计算
5. 调制开始结束日期

项目拓扑图：![拓扑图](https://raw.githubusercontent.com/AlienwareLeon/trendParentProject/master/%E9%A1%B9%E7%9B%AE%E6%8B%93%E6%89%91%E5%9B%BE.png)

----------

# 必要条件

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
**SpringCloud**

## 数据来源本地的第三方数据(JSON)

third-part-index-data-project---resources

## 静态资源

trend-trading-backtest-view--resources

所有指数代码codes.json  closePoint收盘价

----------
# 微服务/端口总结

## 微服务
    
     
    <modules>
    <module>eureka-server</module>
    <module>third-part-index-data-project</module>
    <module>index-gather-store-service</module>
    <module>index-codes-service</module>
    <module>index-data-service</module>
    <module>index-zuul-service</module>
    <module>trend-trading-backtest-service</module>
    <module>trend-trading-backtest-view</module>
    <module>index-hystrix-dashboard</module>
    <module>index-turbine</module>
    <module>index-config-server</module>
    </modules>

## 端口总结

    eureka-server						8761
    third-part-index-data-project		8090
    index-gather-store-service			8001
    index-codes-service				    8011,8012,8013
    index-data-service					8021,8022,8023
    index-zuul-service					8031
    trend-trading-backtest-view			8041,8042,8043
    trend-trading-backtest-service		8051,8052,8053
    index-config-server					8060
    index-hystrix-dashboard				8070
    index-turbine						8080


## 第三方

     
    redis									6379
    zipkin									9411
    rabbitmq								5672
    
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

使用 feign 模式从 INDEX-DATA-SERVICE 微服务获取数据。 
与之前使用的 RestTemplate 方式不同，这里是声明式的,访问不了的时候，就去找 IndexDataClientFeignHystrix 要数据

    fallback = IndexDataClientFeignHystrix.class) 
    
**client--IndexDataClientFeignHystrix**

IndexDataClientFeignHystrix 实现了 IndexDataClient，所以就提供了对应的方法，当熔断发生的时候，对应的方法就会被调用了
如果 INDEX-DATA-SERVICE 不可用或者不可访问，就会返回 0000-00-00 

**service--BackTestService**

提供所有模拟回测数据的微服务

**web--BackTestController**

**启动类：TrendTradingBackTestServiceApplication：**

@EnableFeignClients注解用于启动Feign方式

**修改网关模块 zuul-service 的 application.yml，增加回测数据服务**


## 8.  trend-trading-backtest-view

回测视图

**pom.xml**

增加 spring-boot-starter-thymeleaf jar, 用于对 thymeleaf 进行支持

**ViewController**

控制类

**resources---static**

静态资源



**增加zuul-service里trend-trading-backtest-view网关**


**view.html**

展示页面，比较复杂。。。。


data4Vue ：

	indexs指数
	
	indexDatas 指数数据数组
	dates 日期数组
	closePoints 收盘点数组

	

    init:function()访问网关的 api-codes 获取所有指数代码
	
	changeParamWithFlushDate:function() 切换函数
	
	

chart4Profit 对象

1. 通过 $(".canvas4Profit")[0].getContext('2d') 拿到画布对应的上下文
2. 基于上下文，创建 chart4Profit 对象
3. 类型是 ‘line’： 曲线图
4. 设置相关参数，如颜色，宽度，是否填充等等
5. 设置标题为 指数趋势投资收益对比图
6. responsive：true 表示有新数据的时候会重新画
7. intersect: false和 mode: 'index', 表示 当鼠标移动的时候会自动显示提示信息
8. callbacks: 表示提示信息的格式是： 标签名 ： 取两位小数的数值


## 9. 服务链路追踪zipkin

有多个微服务，分别是代码微服务和数据微服务，网关， 回测微服务，回测视图微服务，随着业务的增加，就会有越来越多的微服务存在，他们之间也会有更加复杂的调用关系

这个调用关系，仅仅通过观察代码，会越来越难以识别，所以就需要通过 zipkin 服务链路追踪服务器 这个东西来用图片进行识别了


       <!--zipkin-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>

		每个微服务都需要加上这个依赖



      zipkin:
    base-url: http://localhost:9411
	每个微服务增加zipkin地址


	
	 
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

	每个微服务启动类，增加Sampler，表示一直在取样


**启动方法：java -jar zipkin-server-2.10.1-exec.jar**

## 10.配置服务器/客户端 index-config-server:8060给8041视图

微服务要做集群，这就意味着，会有多个微服务实例。 在业务上有时候需要修改一些配置信息，比如说 版本信息。 倘若没有配置服务， 那么就需要挨个修改微服务，挨个重新部署微服务，这样就比较麻烦。

这些配置信息放在一个公共的地方，比如git, 然后通过配置服务器把它获取下来，然后微服务再从配置服务器上取下来。 

这样只要修改git上的信息，那么同一个集群里的所有微服务都立即获取相应信息了，这样就大大节约了开发，上线和重新部署的时间了。

先在 git 里保存 version 信息， 然后通过 IndexConfigServer 去获取 version 信息， 接着不同的视图微服务实例再去IndexConfigServer 里获取 version.。

    
    spring:
      application:
    name: index-config-server
      cloud:
    config:
      label: master
      server:
    git:
      uri: https://github.com/xxxx/trendConfig/
      searchPaths: respo
    eureka:
      client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/


> name和eureka 信息略过不表。
> 
> config 配置信息里
> uri 表示 git 地址：
>  
> https://github.com/xxxx/trendConfig/
>  
> 
> label 表示 分支：
>  
> master
>  
> 
> searchPaths 表示目录：
>  
> respo


## 11. 分布式配置--消息总线BUS--rabiitMQ--广播配置服务器获取到的信息

配置服务器和配置客户端的问题是当数据更新后，必须重启配置服务器和配置客户端才能生效。 这个在生产环境肯定是不可接受的。

所以要能够做到实时刷新。 为了做到这一点，我们需要借助于 rabiitMQ 来广播配置服务器获取到的信息。

业务逻辑是（view:8041 就代表端口是 8041 的视图实例）：

1. 通过运行FreshConfigUtil类， 以 post 方式访问地址 http://localhost:8041/actuator/bus-refresh，通知 view:8041 刷新配置。 
2. view:8041 告诉 index-config-server 获取新的配置数据
3. index-config-server 从 git 拿到数据，返回给 view:8041
4. view:8041 拿到数据不仅自己用了，还发给了 rabbitMQ
5. rabbitMQ 拿到这个数据广播给其他的，比如 view:8042


## 12.断路器监控index-turbine
xxx
