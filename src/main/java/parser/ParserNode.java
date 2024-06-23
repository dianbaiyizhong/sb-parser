package parser;

import com.google.common.base.Strings;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ParserNode {


    private Set<String> tableNameList = new HashSet<>();
    private String url;

    private String parentId;
    private String val;
    private String pkg;
    private String className;
    private String methodName;


    public void addTable(String tableName) {
        this.tableNameList.add(tableName);
    }

    public ParserNode(String val) {
        this.val = val;
    }

    public ParserNode(String pkg, String className, String methodName) {
        this.pkg = pkg;
        this.methodName = methodName;
        this.className = className;
        this.val = this.pkg + "." + this.className + "." + this.methodName;
    }

    @Override
    public String toString() {
        return val;
    }
}
