package own.leon.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
public class IndexZuulServiceApplication {
    //http://127.0.0.1:8031/api-codes/codes
    public static void main(String[] args) {
        int port = 8031;
        if (!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d 被占用，无法启动%n",port);
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexZuulServiceApplication.class).properties("server.port="+port).run(args);
    }
}
/*
* 启动类，端口号是 8031.
@EnableZuulProxy 网关
* 为什么Maven加载POM的时候中止一次，项目结构不完全（缺少resources/test目录），手动补全项目结构后
* 启动类里找不到POM引入的JAR，遂删除Module重新建（同名）Module，但是依然解决不了问题。。。
* 最后实在找不到原因，module换了一个名字，结果成了。。。。。
*
* */