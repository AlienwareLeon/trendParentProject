package own.leon.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import own.leon.trend.pojo.Index;
import own.leon.trend.util.SpringContextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "indexes")
public class IndexService {
    private List<Index> indexes;
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        indexes = fetch_indexes_from_third_part();
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }

    @CacheEvict(allEntries = true)
    public void remove(){

    }
    @Cacheable(key = "'all_codes'")
    public List<Index> store() {
        System.out.println(this);
        return indexes;
    }
    @Cacheable(key = "'all_codes'")
    public List<Index> get() {
        return CollUtil.toList();
    }

    public List<Index> fetch_indexes_from_third_part() {
        List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json",List.class); //获取第三方服务的地址
        return map2Index(temp);
    }

    public List<Index> third_part_not_connected() {
        System.out.println("third_part_not_connected()");
        Index index = new Index();
        index.setCode("000000");
        index.setName("无效指令代码");
        return CollectionUtil.toList(index);
    }

    private List<Index> map2Index(List<Map> temp) {
        List<Index> indexes = new ArrayList<>();
        for (Map map : temp) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index = new Index();
            index.setCode(code);
            index.setName(name);
            indexes.add(index);
        }
        return indexes;
    }
} //获取内容是Map类型， map2Index把 Map 转换为 Index


/*
服务类，使用工具类RestTemplate获取地址http://127.0.0.1:8090/indexes/codes.json

@HystrixCommand(fallbackMethod = "third_part_not_connected")
获取失败调用此方法：创建指数对象，返回集合
增加 @CacheConfig(cacheNames="indexes") 表示缓存的名称是 indexes，保存到 redis 就会以 indexes 命名
在fetch_indexes_from_third_part 方法上增加：	@Cacheable(key="'all_codes'") 表示保存到 redis 用的 key 就会使 all_codes.
 */