package own.leon.trend.pojo;

import java.io.Serializable;

public class Index implements Serializable{
    //在NOSQL中，没用与Java基本类型对应的数据结构，向nosql存储时，必须将对象序列化
    String code;
    String name;

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
