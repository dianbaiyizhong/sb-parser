package parser;

import cn.hutool.core.collection.CollectionUtil;

import java.util.*;
import java.util.stream.Collectors;


public class DagUtil {
    public static List<List<String>> getAllPath(List<Node> vertices,
                                                List<Edge> edges,
                                                String currentVertexId) {
        List<String> pathList = new ArrayList<>();
        List<List<String>> finalPath = new ArrayList<>();

        getPathList(pathList, vertices, edges, currentVertexId, finalPath);

        return finalPath;
    }

    /**
     * 递归获取起始点的所有路径
     *
     * @param path            临时路径
     * @param vertices        所有顶点
     * @param edges           所有边
     * @param currentVertexId 当前顶点
     * @param finalPath       最终路径
     */
    private static void getPathList(List<String> path,
                                    List<Node> vertices,
                                    List<Edge> edges,
                                    String currentVertexId,
                                    List<List<String>> finalPath) {
        //路径添加当前顶点
        path.add(currentVertexId);
        //判断当前顶点是否是终点
        List<Node> nextVertexList = getNextVertexList(currentVertexId, vertices, edges);
        //没有后续相邻顶点视为终点
        if (CollectionUtil.isNotEmpty(nextVertexList)) {
            for (int i = 0; i < nextVertexList.size(); i++) {
                getPathList(path, vertices, edges, nextVertexList.get(i).getId(), finalPath);
            }
        } else {
            //当前路径加入结果路径集合
            List<String> road = new ArrayList<>(path);
            finalPath.add(road);
        }
        //删除最后一位
        path.remove(path.size() - 1);
    }

    /**
     * 获取顶点的相邻顶点集合
     *
     * @param vertexId
     * @param vertices
     * @param edges
     * @return
     */
    private static List<Node> getNextVertexList(
            String vertexId,
            List<Node> vertices,
            List<Edge> edges) {
        List<Node> nextVertextList = vertices.stream().filter(v -> {
            for (Edge edge : edges) {
                if (edge.getFrom().equals(vertexId) && edge.getTo().equals(v.getId())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return nextVertextList;
    }


}
