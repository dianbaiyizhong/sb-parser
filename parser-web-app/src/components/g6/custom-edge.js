import G6 from "@antv/g6";
import uniqueId from 'lodash/uniqueId';

const MIN_ARROW_SIZE = 3

let runningEdgeColor = "#5FD38F"
let commonEdgeColor = "#808080"

function isRunning(sourceModelState, targetModelState) {

  if (sourceModelState == 1 && targetModelState == 1) {
    return true
  } else {
    return false
  }
}

const customEdge = {


  init() {

    G6.registerEdge('customEdge', {
      draw(cfg, group) {
        let sourceNode, targetNode, start, end
        if (typeof (cfg.source) === 'string') {
          cfg.source = cfg.sourceNode
        }
        if (!cfg.start) {
          cfg.start = {
            x: 0,
            y: 17
          }
        }
        if (!cfg.end) {
          cfg.end = {
            x: 0,
            y: -17
          }
        }


        sourceNode = cfg.sourceNode._cfg.model
        start = {
          x: sourceNode.x + cfg.start.x,
          y: sourceNode.y + cfg.start.y,
        }


        // console.info(cfg)
        if (typeof (cfg.target) === 'string') {
          targetNode = cfg.targetNode._cfg.model
        } else if (!cfg.target.x) {
          targetNode = cfg.target._cfg.model
        } else {
          targetNode = cfg.target
        }
        end = {
          x: targetNode.x + cfg.end.x,
          y: targetNode.y + cfg.end.y,
        }


        let path = []
        let hgap = Math.abs(end.x - start.x)
        if (end.x > start.x) {
          path = [
            ['M', start.x, start.y],
            [
              'C',
              start.x,
              start.y + hgap / (hgap / 50),
              end.x,
              end.y - hgap / (hgap / 50),
              end.x,
              end.y - 4
            ],
            [
              'L',
              end.x,
              end.y
            ]
          ]
        } else {
          path = [
            ['M', start.x, start.y],
            [
              'C',
              start.x,
              start.y + hgap / (hgap / 50),
              end.x,
              end.y - hgap / (hgap / 50),
              end.x,
              end.y - 4
            ],
            [
              'L',
              end.x,
              end.y
            ]
          ]
        }


        const keyShape = group.addShape('path', {
          attrs: {
            id: 'edge' + uniqueId(),
            path: path,
            stroke: commonEdgeColor,
            lineWidth: 1,
            endArrow: {
              path: G6.Arrow.triangle(6, 4, 0),
              lineDash: [0, 0],
              fill: commonEdgeColor,
              d: 0
            }
          }
        });
        return keyShape
      },
      afterDraw(cfg, group) {

        if (cfg.targetNode == null) {
          // 拖拽连线但是未找到终点的时候，需要有这个判断
          return
        }

        const shape = group.get('children')[0];
        // 判断并启动连线动画

        if (isRunning(cfg.source.getModel().nodeState, cfg.targetNode._cfg.model.nodeState)) {
          // 设置动画流水线图为绿色
          shape.attr("stroke", runningEdgeColor);
          shape.attr("lineWidth", "2");
          let index = 0;
          // 边 path 图形的动画
          shape.animate(
            () => {
              index++;
              if (index > 10) {
                index = 0;
              }
              const res = {
                lineDash,
                lineDashOffset: -index,
              };
              return res;
            },
            {
              repeat: true,
              duration: 2000,
            },
          );
        } else {
          shape.attr("lineWidth", "1");
        }
      },
      setState(name, value, item) {

        let running = isRunning(item._cfg.source._cfg.model.nodeState, item._cfg.target._cfg.model.nodeState)

        // 判断该连线是否在流水中


        const group = item.getContainer();
        const shape = group.get("children")[0];

        const selectStyles = () => {

          shape.attr("stroke", "#6ab7ff");

        };
        const unSelectStyles = () => {
          if (running) {
            shape.attr("stroke", runningEdgeColor);
          } else {
            shape.attr("stroke", commonEdgeColor);
          }
        };

        switch (name) {
          case "selected":
          case "hover":
            if (value) {
              selectStyles()
            } else {
              unSelectStyles()
            }
            break;
        }
      }
    });


    const lineDash = [10, 2];

    G6.registerEdge('link-edge', {
      draw(cfg, group) {
        let sourceNode, targetNode, start, end

        sourceNode = cfg.sourceNode._cfg.model

        start = {
          x: sourceNode.x + cfg.start.x,
          y: sourceNode.y + cfg.start.y,
        }
        end = {
          x: cfg.endPoint.x + cfg.end.x,
          y: cfg.endPoint.y,
        }
        let path = []
        path = [
          ['M', start.x, start.y],
          ['L', end.x, end.y]
        ]
        const keyShape = group.addShape('path', {
          attrs: {
            id: 'edge' + uniqueId(),
            path: path,
            stroke: '#1890FF',
            strokeOpacity: 0.9,
            lineDash: [5, 5]
          }
        });
        return keyShape
      },
    });
  }
}

export default customEdge
