package parser;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;

public class ParserNodeFactory {


    private static List<String> sqlOpType = Lists.newArrayList("select", "delete", "insert", "update");

    public static Map<String, Set<String>> getDaoNode(String projectPath, List<String> tableNameList) {

        Map<String, Set<String>> retList = new HashMap<>();
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
                JSONArray insert = mapper.getJSONArray(op);
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

        return retList;

    }


}
