<template>
  <div id="main" style="width: 100vw;height: 100vh"></div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'App',
  components: {},
  data() {
    return {
      myChart: null,
      json: {
        "nodes": [{
          "id": "com.nntk.sb.controller.UserInfoController.queryApi",
          "name": "com.nntk.sb.controller.UserInfoController.queryApi",
          "type": 1
        }, {
          "id": "com.nntk.sb.service.UserInfoService.queryUser",
          "name": "com.nntk.sb.service.UserInfoService.queryUser",
          "type": 0
        }, {
          "id": "com.nntk.sb.service.log.info",
          "name": "com.nntk.sb.service.log.info",
          "type": 0
        }, {
          "id": "com.nntk.sb.manager.UserInfoManager.getSomethingByRedis",
          "name": "com.nntk.sb.manager.UserInfoManager.getSomethingByRedis",
          "type": 0
        }, {"id": "t_user", "name": "t_user", "type": 2}, {
          "id": "com.nntk.sb.mapper.TUserMapper.selectByExample",
          "name": "com.nntk.sb.mapper.TUserMapper.selectByExample",
          "type": 0
        }, {
          "id": "com.nntk.sb.service.UserInfoService.queryOrder",
          "name": "com.nntk.sb.service.UserInfoService.queryOrder",
          "type": 0
        }, {
          "id": "com.nntk.sb.service.log.warn",
          "name": "com.nntk.sb.service.log.warn",
          "type": 0
        }, {
          "id": "com.nntk.sb.controller.UserInfoController.queryOrderApi",
          "name": "com.nntk.sb.controller.UserInfoController.queryOrderApi",
          "type": 1
        }]
        ,
        "edges": [{
          "from": "com.nntk.sb.controller.UserInfoController.queryApi",
          "to": "com.nntk.sb.service.UserInfoService.queryUser"
        }, {
          "from": "com.nntk.sb.service.UserInfoService.queryUser",
          "to": "com.nntk.sb.service.log.info"
        }, {
          "from": "com.nntk.sb.service.UserInfoService.queryUser",
          "to": "com.nntk.sb.manager.UserInfoManager.getSomethingByRedis"
        }, {
          "from": "com.nntk.sb.manager.UserInfoManager.getSomethingByRedis",
          "to": "t_user"
        }, {
          "from": "com.nntk.sb.manager.UserInfoManager.getSomethingByRedis",
          "to": "com.nntk.sb.mapper.TUserMapper.selectByExample"
        }, {
          "from": "com.nntk.sb.controller.UserInfoController.queryApi",
          "to": "com.nntk.sb.service.UserInfoService.queryOrder"
        }, {
          "from": "com.nntk.sb.service.UserInfoService.queryOrder",
          "to": "com.nntk.sb.service.log.warn"
        }, {
          "from": "com.nntk.sb.service.UserInfoService.queryOrder",
          "to": "t_user"
        }, {
          "from": "com.nntk.sb.service.UserInfoService.queryOrder",
          "to": "com.nntk.sb.mapper.TUserMapper.selectByExample"
        }, {
          "from": "com.nntk.sb.controller.UserInfoController.queryOrderApi",
          "to": "com.nntk.sb.service.UserInfoService.queryOrder"
        }]

      }
    }
  },

  methods: {
    // 高亮所有关联节点的函数
    highlightRelatedNodes: function (nodeId) {
      let that = this;
      // 获取所有节点的 option 数据
      let option = this.myChart.getOption();
      let series = option.series[0];
      let nodes = series.data;
      let edges = series.edges;
      // 查找特定节点
      let focusedNode;
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].id === nodeId) {
          focusedNode = nodes[i];
          break;
        }
      }

      // 高亮所有相关联的节点
      if (focusedNode) {
        console.info(nodes.indexOf(focusedNode))
        // that.myChart.dispatchAction({
        //   type: 'highlight',
        //   seriesIndex: 0,
        //   dataIndex: nodes.indexOf(focusedNode)
        // });
        // 遍历所有边，找到相关联的节点并高亮它们
        edges.forEach(function (edge) {
          let relatedNode;
          if (edge.source === nodeId) {
            relatedNode = edge.target;
          } else if (edge.target === nodeId) {
            relatedNode = edge.source;
          }
          if (relatedNode) {
            let relatedIndex = nodes.findIndex(function (node) {
              return node.id === relatedNode;
            });
            if (relatedIndex >= 0) {
              that.myChart.dispatchAction({
                type: 'highlight',
                seriesIndex: 0,
                dataIndex: relatedIndex
              });
            }
          }
        });
      }
    }
  },
  created() {

  },
  mounted() {
    this.myChart = echarts.init(document.getElementById('main'));
    let option = {
      title: {
        text: 'NPM Dependencies'
      },
      animationDurationUpdate: 1500,
      animationEasingUpdate: 'quinticInOut',
      series: [
        {
          type: 'graph',
          layout: 'force',
          force: {
            repulsion: 3000,
            edgeLength: [120, 500]
          },
          // progressiveThreshold: 700,
          data: this.json.nodes.map(function (node) {
            let symbolSize = 30
            let color = "#c78419"
            let showLabel = false
            if (node.type === 2) {
              symbolSize = 100
              color = '#19c719'
              showLabel = true
            }
            if (node.type === 1) {
              symbolSize = 50
              color = '#1984c7'
              showLabel = true
            }
            return {
              id: node.id,
              name: node.name,
              symbolSize: symbolSize,
              itemStyle: {
                color: color
              },
              label: {
                show: showLabel
              }
            };
          }),
          edges: this.json.edges.map(function (edge) {
            return {
              source: edge.from,
              target: edge.to,
            };
          }),
          draggable: true,
          edgeSymbol: ['circle', 'arrow'], // 边两端的标记类型，可以是'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow', 'none'
          // emphasis: {
          //   focus: 'adjacency',
          //   label: {
          //     position: 'right',
          //     show: true
          //   }
          // },
          roam: true,
          lineStyle: {
            width: 0.5,
            curveness: 0.3,
            opacity: 0.7
          }
        }
      ]
    }
    this.myChart.setOption(option);

    let that = this;
    this.myChart.on('click', function (params) {
      // 点击事件触发后，重新设置 option 中的 focusNodeAdjacency 属性
      // 将 params.data 设置为焦点节点
      that.highlightRelatedNodes(params.data.id)
    });

  }
}
</script>

<style>

</style>
