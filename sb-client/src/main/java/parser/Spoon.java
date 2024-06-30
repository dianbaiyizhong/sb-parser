package parser;

import cn.hutool.core.io.FileUtil;
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
import java.util.stream.Collectors;

@Slf4j
public class Spoon {


    private static List<ParserNode> parserNodeList = new ArrayList<>();

    public static void main(String[] args) {

        List<String> tableName = new ArrayList<>();
        tableName.add("t_user");


        // 获取上一级目录
        File parent = FileUtil.getParent(new File(System.getProperty("user.dir")), 1);
        String projectPath = parent + File.separator + "sb-server" + File.separator + "src" + File.separator + "main";

        Map<String, Set<String>> daoNodeMap = ParserNodeFactory.getDaoNode(projectPath, tableName);


        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setComplianceLevel(17);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        CtModel ctModel = launcher.getModel();
        List<ParserNode> parserNodes = new ArrayList<>();

        // 定义一个map，装载接口和实现累之间的关系
        Map<String, String> interfaceMap = new HashMap<>();
        // 获取所有类
        Collection<CtType<?>> allTypes = ctModel.getAllTypes();
        for (CtType<?> item : allTypes) {
            String implClass = item.getPackage() + "." + item.getSimpleName();
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
        // 解析接口类
        List<CtElement> elements = SpoonUtil.findWithAnnotation(ctModel, RequestMapping.class);
        for (CtElement element : elements) {
            CtMethod<?> method = (CtMethod<?>) element;
            String apiUrl = element.getAnnotations().get(0).getValues().get("value").toString().replaceAll("\"", "");
            CtType<?> declaringType = method.getDeclaringType();
            String packageName = declaringType.getPackage().getQualifiedName();
            String className = declaringType.getSimpleName();
            ParserNode parserNode = new ParserNode(packageName, className, method.getSimpleName(), NodeType.API.getType());
            parserNode.setName(apiUrl);
            parserNodes.add(parserNode);
        }

        for (ParserNode parserNode : parserNodes) {
            parserNodeList.add(parserNode);
            findNextCalls(ctModel, parserNode, interfaceMap, daoNodeMap);
        }


        List<Node> nodeList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();

        for (int i = 0; i < parserNodeList.size(); i++) {
            ParserNode parserNode = parserNodeList.get(i);

            Node buildNode = Node.builder()
                    .id(parserNode.getVal())
                    .name(parserNode.getName())
                    .type(parserNode.getType())
                    .build();


            nodeList.add(buildNode);

            if (parserNode.getParentId() != null) {
                edgeList.add(Edge.builder()
                        .from(parserNode.getParentId())
                        .to(parserNode.getVal())
                        .build());
            }

        }

        nodeList = nodeList.stream().distinct().toList();
        System.out.println(JSON.toJSONString(nodeList));
        edgeList = edgeList.stream().distinct().toList();
        System.out.println(JSON.toJSONString(edgeList));


        // 将关系倒转，方便从表开始追溯
        for (Edge edge : edgeList) {
            String edgeTo = edge.getTo();
            String edgeFrom = edge.getFrom();
            edge.setFrom(edgeTo);
            edge.setTo(edgeFrom);
        }


        // 过滤出所有表
        List<Node> tableNodeList = nodeList.stream().filter(node -> node.getType() == NodeType.TABLE.getType()).toList();


        Map<String, Object> releationMap = new HashMap<>();
        for (Node node : tableNodeList) {
            List<List<String>> allPath = DagUtil.getAllPath(nodeList, edgeList, node.getId());
            releationMap.put(node.getId(), allPath);

        }

        System.out.println(JSON.toJSONString(releationMap));
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
                    String newMethodName = invocation.getExecutable().getSimpleName();
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
                        for (String daoNode : daoNodeMap.get(itemName)) {
                            ParserNode tableNode = new ParserNode(daoNode);
                            tableNode.setName(daoNode);
                            tableNode.setType(NodeType.TABLE.getType());
                            tableNode.setParentId(srcKey);
                            parserNodeList.add(tableNode);
                        }
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