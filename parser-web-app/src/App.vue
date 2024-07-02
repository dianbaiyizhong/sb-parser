<template>
  <v-app id="g6_container" style="width: 100vw;height: 100vh;">

    <v-app-bar class="my-header">
      <v-container class='d-flex align-center py-0'>
        <v-app-bar-title class='pl-0'>
          <div class='d-flex align-center'>
            <v-avatar rounded='0' class='mr-3' image='./assets/blood.png'/>
            API表映射可视化
          </div>
        </v-app-bar-title>


        <v-spacer></v-spacer>
        <v-menu>
          <template v-slot:activator="{ props }">
            <v-btn
              color="primary"
              v-bind="props"
            >
              布局
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              @click='layout(item)'
              v-for="(item, index) in layoutItems"
              :key="index"
              :value="index"
            >
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
        <v-btn icon @click="fit">
          <v-icon>mdi-fit-to-screen-outline</v-icon>
        </v-btn>

      </v-container>
    </v-app-bar>
    <AppFooter/>
  </v-app>
</template>

<script>
import axios from 'axios';
import G6 from '@antv/g6';
import customNode from "./components/g6/custom-node";
import customEdge from './components/g6/custom-edge'
import hoverNode from './components/g6/hover-node'
import selectNode from './components/g6/select-node'
import bus from '@/plugins/bus';

import edges from './data/edges.json';
import nodes from './data/nodes.json';

let currentSelectModel = null
import {nextTick} from "vue";

let graph = null

export default {
  name: 'App',
  components: {},
  data() {
    return {
      layoutItems: [
        {title: 'force2力导向', id: 'force2'},
        {title: 'force力导向', id: 'force'},
        {title: '环形', id: 'circular'},
        {title: '辐射形', id: 'radial'},
        {title: '同心圆', id: 'concentric'},
        {title: '层次-从上至下布局', type: 'TB', 'id': 'dagre'},
        {title: '层次-从下至上布局', type: 'BT', 'id': 'dagre'},
        {title: '层次-从左至右布局', type: 'LR', 'id': 'dagre'},
        {title: '层次-从右至左布局', type: 'RL', 'id': 'dagre'},
        {title: 'Fruchterman', id: 'fruchterman'},
        {title: '高维数据降维', id: 'mds'},
        {title: '格子', id: 'grid'},
        {title: 'GForce', id: 'gForce'},
        {title: 'GForce', id: 'gForce'},
        {title: 'Force Atlas 2', id: 'forceAtlas2'},
        {title: 'Combo 力导向', id: 'comboForce'},
        {title: 'Combo 复合布局', id: 'comboCombined'},
      ],
      nodes: nodes,
      edges: edges


    }
  },

  methods: {
    layout: function (item) {
      if (item.id === 'dagre') {
        graph.updateLayout({
          type: item.id,
          rankdir: item.type,
        })
        return
      }
      graph.updateLayout({
        type: item.id
      })
    },

    fit: function () {

      graph.fitCenter()

    },

    init: function () {
      let that = this

      let nodes = this.nodes.map(function (node) {
        return {
          id: node.id,
          nodeId: node.id,
          type: "customNode",
          nodeType: node.type,
          color: "#1890ff",
          nodeName: node.name,
          nodeState: 0,
        }
      })

      let edges = this.edges.map(function (edge) {
        return {
          source: edge.from,
          target: edge.to,
        }
      })

      let data = {
        nodes,
        edges
      };

      const grid = new G6.Grid();

      const container = document.getElementById("g6_container")
      const width = container.scrollWidth;
      const height = container.scrollHeight;

      graph = new G6.Graph({
        container: 'g6_container',
        width,
        height,
        enabledStack: true,
        modes: {
          default: [
            "drag-canvas",
            "zoom-canvas",
            "hover-node",
            "select-node",
            "hover-edge",
            "keyboard",
            "customer-events",
            "add-menu",
            {
              type: "drag-node",
            },
          ],
        },
        plugins: [grid], // 配置 Grid 插件和 Minimap 插件
        defaultEdge: {
          type: 'quadratic',
          style: {
            stroke: "#F6BD16",
            lineWidth: 2,
          },
        },
        groupByTypes: true,
        layout: {
          type: 'dagre',
          rankdir: 'TB',

        },
      });

      graph.edge(() => {
        return {
          type: "customEdge",
        }
      })


      graph.read(data)
      graph.render()
      graph.fitView()
      setTimeout(function () {
        graph.fitCenter()
      }, 100)


      // graph.on('node:mouseenter', (evt) => {
      //   const {item} = evt;
      //   graph.setItemState(item, 'hover', true);
      // })
      //
      // graph.on('node:mouseleave', (evt) => {
      //   const {item} = evt;
      //   graph.setItemState(item, 'hover', false);
      // })

      // bus.$on('nodeselectchange', (item) => {
      //   if (item.select === true && item.target.getType() === "node") {
      //     self.status = "node-selected"
      //     self.item = item.target
      //     self.node = item.target.getModel()
      //   } else {
      //     self.status = "canvas-selected"
      //     self.item = null
      //     self.node = null
      //   }
      // });


      const releationMap = {"t_achievement_normal": [["t_achievement_normal", "com.zhenmei.p7i.rest.service.impl.AchievementServiceImpl.listNormalByUserId", "com.zhenmei.p7i.rest.web.controller.AchievementController.listNormal"]]}
      bus.$on("nodeselectchange", item => {
        if (item.select === true && item.target.getType() === "node") {
          let selectModel = item.target._cfg.model
          currentSelectModel = selectModel
          if (selectModel.nodeType == 2) {
            releationMap[selectModel.nodeId].forEach(function (items) {
              items.forEach(function (item) {
                // let node = that.$_.find(nodes, {'nodeId': item})
                // console.info(node)
                // node.nodeState = 1

                let node = graph.findById(item)
                // console.info(node)
                // node._cfg.model.nodeState = 1
                let model = node._cfg.model
                model.nodeState = 1
                // graph.refreshItem(node);
                graph.updateItem(node, model);


              })


            })
            // graph.changeData(data)
            // 由于changeData后，选中状态又取消了，这里手动设置为selected状态
            // graph.setItemState(selectModel.nodeId, 'selected', true);
          }
        } else {

          // graph.getNodes().forEach(function (node) {
          //   node.nodeState = 0
          // })
          const nodes = graph.findAll('node', (node) => {
            return node.get('model').nodeState === 1;
          })
          nodes.forEach(function (item) {
            let node = graph.findById(item._cfg.model.nodeId)
            // node._cfg.model.nodeState = 0
            let model = node._cfg.model
            model.nodeState = 0
            graph.updateItem(node, model);

          })

          graph.setItemState(currentSelectModel.nodeId, 'selected', false);
          // graph.changeData(data)

        }
      })
    }
  },
  created() {
    customNode.init()
    customEdge.init()

    const behavors = {
      'hover-node': hoverNode,
      'select-node': selectNode,
    }
    for (let key in behavors) {
      G6.registerBehavior(key, behavors[key])
    }

  },
  mounted() {

    let that = this

    nextTick(new function () {
      that.init()
    })


  }
}

</script>
<style lang="scss">
.my-header {
  background-color: rgb(0, 0, 0, 0) !important;
}

// g6 主体位置
canvas {
  position: relative;
  z-index: 1;
}

// 网格位置
.g6-grid-container {
  z-index: 0 !important;

  *,
  ::before,
  ::after {
    // 覆盖 vuetify  background-repeat: no-repeat;
    background-repeat: repeat;
  }
}
</style>
