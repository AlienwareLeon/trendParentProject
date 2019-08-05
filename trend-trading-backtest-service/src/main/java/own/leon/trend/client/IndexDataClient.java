package own.leon.trend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import own.leon.trend.pojo.IndexData;

import java.util.List;

@FeignClient(value = "INDEX-DATA-SERVICE",fallback = IndexDataClientFeignHystrix.class)
public interface IndexDataClient {
    @GetMapping("/data/{code}")
    public List<IndexData> getIndexData(@PathVariable("code") String code);
}
/*
* 使用feign模式从INDEX-DATA-SERVICE微服务获取数据
* 与RestTemplate方式不同，使用声明式
*fallback访问不了，访问 IndexDataClientFeignHystrix 要数据
* */