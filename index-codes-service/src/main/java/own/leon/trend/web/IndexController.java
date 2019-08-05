package own.leon.trend.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import own.leon.trend.config.IpConfiguration;
import own.leon.trend.pojo.Index;
import own.leon.trend.service.IndexService;

import java.util.List;

@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    IpConfiguration ipConfiguration;

    //http://127.0.0.1:8011/codes

    @GetMapping("/codes")
    @CrossOrigin
    public List<Index> codes() throws Exception {
        System.out.println("current instance's port is" + ipConfiguration.getPort());
        return indexService.get();
    }
}
/*
* 返回代码集合，并通过IpConfiguration获取当前接口并打印
*@CrossOrigin 表示允许跨域，因为后续的回测视图是另一个端口号的，访问这个服务是属于跨域了
* */
