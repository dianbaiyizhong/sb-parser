package parser;

import com.google.common.base.Strings;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ParserNode {


    private int type = 0; // 0:代表方法节点，1:代表接口，2:代表表名
    private String url;
    private String parentId;
    private String val;
    private String pkg;
    private String className;
    private String methodName;
    private String name;


    public ParserNode(String val) {
        this.val = val;
        this.name = this.val;
    }

    public ParserNode(String pkg, String className, String methodName) {
        this.pkg = pkg;
        this.methodName = methodName;
        this.className = className;
        this.val = this.pkg + "." + this.className + "." + this.methodName;
        this.name = this.val;
    }


    public ParserNode(String pkg, String className, String methodName, int type) {
        this(pkg, className, methodName);
        this.type = type;
    }

    @Override
    public String toString() {
        return val;
    }
}
