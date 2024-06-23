package parser;

import lombok.Data;
import lombok.Getter;

@Getter
public enum NodeType {
    API("API", 1), METHOD("METHOD", 0), TABLE("TABLE", 2);
    private String name;
    private int type;

    //构造方法
    NodeType(String name, int type) {
        this.name = name;
        this.type = type;
    }
}
