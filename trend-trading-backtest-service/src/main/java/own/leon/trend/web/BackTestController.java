package own.leon.trend.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import own.leon.trend.pojo.IndexData;
import own.leon.trend.service.BackTestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BackTestController {

    @Autowired
    BackTestService backTestService;

    @GetMapping("/simulate/{code}")
    @CrossOrigin
    public Map<String,Object> backTest(@PathVariable("code") String code) throws Exception {
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
        Map<String,Object> result = new HashMap<>();
        result.put("indexDates",allIndexDatas);
        return result;
    }
}
/*
* 控制器，返回的数据是放在Map里面，目前的key是indexDatas
* 将来返回数据，区分不同的数据
*
* */