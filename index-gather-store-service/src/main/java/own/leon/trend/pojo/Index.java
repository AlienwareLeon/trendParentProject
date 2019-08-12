package own.leon.trend.pojo;

import java.io.Serializable;

public class Index implements Serializable {
    String code;  //指数名称
    String name; //指数代码

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
