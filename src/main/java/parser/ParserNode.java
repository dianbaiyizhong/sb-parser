package parser;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ParserNode {


    private Set<String> tableNameList = new HashSet<>();
    private String val;
    private String pkg;
    private String className;
    private String methodName;

    public ParserNode(String pkg, String className, String methodName) {
        this.pkg = pkg;
        this.methodName = methodName;
        this.className = className;
    }


}
