package own.leon.trend.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent>{

    private int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event){
        this.serverPort = event.getWebServer().getPort();
    }

    public int getPort() {
        return this.serverPort;
    }
}
/*
* 获取当前端口号，微服务做集群，不同微服务使用
*端口号不同，通过获取端口号打印知道当前是哪个微服务
*
* */