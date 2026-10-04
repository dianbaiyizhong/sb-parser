package parser;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.CtAnnotationTypeImpl;
import spoon.support.reflect.declaration.CtInterfaceImpl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;

public class ParserNodeFactory {


    private static List<String> sqlOpType = Lists.newArrayList("select", "delete", "insert", "update");

    public static void getDaoNode(String projectPath, List<String> tableNameList, Map<String, Set<String>> retList) {

        List<File> files = FileUtil.loopFiles(new File(projectPath), file -> {
            if (FileUtil.getSuffix(file).equals("xml")) {
                // 适配xml方式的mybatis
                JSONObject entries = JSONUtil.xmlToJson(FileUtil.readString(file, Charset.defaultCharset()));
                // 如果根节点为mapper，则判定为mybatis的xml
                if (!entries.containsKey("mapper")) {
                    return false;
                }

                return true;
            } else {
                return false;
            }
        });
        for (File file : files) {
            JSONObject entries = JSONUtil.xmlToJson(FileUtil.readString(file, Charset.defaultCharset()));
            JSONObject mapper = entries.getJSONObject("mapper");
            String nameSpace = mapper.getStr("namespace");
            for (String op : sqlOpType) {
                if (!mapper.containsKey(op)) {
                    continue;
                }
                Object objOrArr = mapper.get(op);
                JSONArray insert = new JSONArray();

                if (objOrArr instanceof JSONArray) {
                    insert = mapper.getJSONArray(op);
                } else {
                    JSONObject jsonObject = mapper.getJSONObject(op);
                    insert.add(jsonObject);
                }
                for (int i = 0; i < insert.size(); i++) {
                    JSONObject item = insert.getJSONObject(i);
                    String sql = item.getStr("content");
                    String id = item.getStr("id");
                    Set<String> tableSet = new HashSet<>();
                    for (String tableName : tableNameList) {
                        if (StringUtils.containsIgnoreCase(sql, tableName)) {
                            tableSet.add(tableName);
                        }
                    }
                    retList.put(nameSpace + "." + id, tableSet);

                }
            }
        }


    }

    public static void getDaoNode(CtModel ctModel, List<String> tableNameList, Map<String, Set<String>> retList) {
        List<CtMethod> methodElements = ctModel.getRootPackage().getElements(new TypeFilter<>(CtMethod.class));
        for (CtMethod methodElement : methodElements) {
            if (methodElement.getParent() instanceof CtInterfaceImpl) {
                continue;
            }
            if (methodElement.getParent() instanceof CtAnnotationTypeImpl<?>) {
                continue;
            }

            CtClass cz = (CtClass) methodElement.getParent();
            String curClazz = cz.getSimpleName();
            String curPkg = null != cz.getPackage() ? cz.getPackage().getQualifiedName() : "-";

            String packageName = curPkg + "." + curClazz + "." + methodElement.getSimpleName();

            for (String tableName : tableNameList) {
                String camelCaseStr = StrUtil.toCamelCase(tableName);
                if (methodElement.toString().toLowerCase().contains(camelCaseStr.toLowerCase())) {
                    if (retList.containsKey(packageName)) {
                        retList.get(packageName).add(tableName);
                    } else {
                        retList.put(packageName, Sets.newHashSet(tableName));
                    }

                }
            }
        }
    }


}
