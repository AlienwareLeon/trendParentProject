package own.leon.trend.client;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Component;
import own.leon.trend.pojo.IndexData;

import java.util.List;

@Component
public class IndexDataClientFeignHystrix implements IndexDataClient{

    @Override
    public List<IndexData> getIndexData(String code) {
        IndexData indexData = new IndexData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }
}

/*
* IndexDataClientFeignHystrix 实现了 IndexDataClient，提供了对应的方法，
* 当熔断发生，对应的方法被调用
如果 INDEX-DATA-SERVICE 不可用或者不可访问，返回 0000-00-00
*
*
*
* */
