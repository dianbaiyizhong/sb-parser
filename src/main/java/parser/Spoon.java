package parser;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;

import java.io.File;
import java.util.*;

@Slf4j
public class Spoon {


    private static List<ParserNode> parserNodeList = new ArrayList<>();

    public static void main(String[] args) {

        List<String> tableName = new ArrayList<>();
        tableName.add("t_user");
        String projectPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main";

        Map<String, Set<String>> daoNodeMap = ParserNodeFactory.getDaoNode(projectPath, tableName);


        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setComplianceLevel(8);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        CtModel ctModel = launcher.getModel();
        List<ParserNode> nodeList = new ArrayList<>();

        // 定义一个map，装载接口和实现累之间的关系
        Map<String, String> interfaceMap = new HashMap<>();
        // 获取所有类
        Collection<CtType<?>> allTypes = ctModel.getAllTypes();
        for (CtType<?> item : allTypes) {
            String implClass = item.getPackage() + "." + item.getActualClass().getSimpleName();
            if (item instanceof CtClass) {
                // 获取有实现类的接口
                CtTypeReference<?> ctTypeReference = ((CtClass) item).getSuperInterfaces().stream().findFirst().orElse(null);
                if (ctTypeReference != null) {
                    // 获取接口的所有方法
                    List<CtMethod<?>> methodList = ctTypeReference.getDeclaration().getMethods().stream().toList();
                    for (CtMethod<?> ctMethod : methodList) {
                        String interfaceClass = ctTypeReference.getPackage() + "." + ctTypeReference.getSimpleName() + "." + ctMethod.getSimpleName();
                        interfaceMap.put(interfaceClass, implClass);
                    }
                }
            }

        }
        System.out.println(interfaceMap);

        // 解析接口类
        List<CtElement> elements = SpoonUtil.findWithAnnotation(ctModel, RequestMapping.class);
        for (CtElement element : elements) {
            CtMethod<?> method = (CtMethod<?>) element;
            CtType<?> declaringType = method.getDeclaringType();
            String packageName = declaringType.getPackage().getQualifiedName();
            String className = declaringType.getSimpleName();
            nodeList.add(new ParserNode(packageName, className, method.getSimpleName()));
        }

        for (ParserNode parserNode : nodeList) {
            parserNode.setParentId("root");
            parserNodeList.add(parserNode);
            findNextCalls(ctModel, parserNode, interfaceMap, daoNodeMap);
        }
        TreeNodeConfig nodeConfig = new TreeNodeConfig();
        nodeConfig.setIdKey("val");
        nodeConfig.setNameKey("val");
        nodeConfig.setParentIdKey("parent_id");

        List<Tree<String>> root = TreeUtil.build(parserNodeList, "root", nodeConfig, (parserNode, tree) -> {

            tree.setId(parserNode.getVal());
            tree.setName(parserNode.getVal());
            tree.setParentId(parserNode.getParentId());
            tree.putExtra("tableNameList", parserNode.getTableNameList());
        });


        System.out.println(JSON.toJSONString(root));

    }


    public static void findNextCalls(CtModel ctModel, ParserNode node, Map<String, String> interfaceMap, Map<String, Set<String>> daoNodeMap) {
        String srcKey = node.getVal();
        ctModel.getElements(new TypeFilter<CtMethod<?>>(CtMethod.class) {
            @Override
            public boolean matches(CtMethod<?> method) {
                if (!(method.getParent() instanceof CtClass)) {
                    return false;
                }
                CtClass cz = (CtClass) method.getParent();
                String curClazz = cz.getSimpleName();
                String curPkg = null != cz.getPackage() ? cz.getPackage().getQualifiedName() : "-";
                return srcKey.equals(curPkg + "." + curClazz + "." + method.getSimpleName());
            }
        }).forEach(m -> {
            List<CtInvocation<?>> invocations = m.getElements(new TypeFilter<>(CtInvocation.class));
            for (CtInvocation<?> invocation : invocations) {
                try {
                    String newPkg = invocation.getExecutable().getDeclaringType().getPackage().getQualifiedName();
                    String newMethodName = invocation.getExecutable().getActualMethod().getName();
                    String newClassName = invocation.getExecutable().getDeclaringType().getSimpleName();
                    String itemName = newPkg + "." + newClassName + "." + newMethodName;

                    ParserNode parserNode = null;
                    // 如果匹配，则要用实现类来继续递归
                    if (interfaceMap.containsKey(itemName)) {
                        parserNode = new ParserNode(interfaceMap.get(itemName) + "." + newMethodName);
                    } else {
                        parserNode = new ParserNode(newPkg, newClassName, newMethodName);
                    }

                    if (daoNodeMap.containsKey(itemName)) {
                        parserNode.setTableNameList(daoNodeMap.get(itemName));
                    }

                    parserNode.setParentId(srcKey);
                    parserNodeList.add(parserNode);
                    findNextCalls(ctModel, parserNode, interfaceMap, daoNodeMap);
                } catch (SpoonClassNotFoundException e) {
                    log.warn(e.getMessage());
                }
            }
        });
    }


}