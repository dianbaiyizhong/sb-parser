import G6 from "@antv/g6";
import uniqueId from 'lodash/uniqueId';
import okSvg from "./icons/ok.svg";
import loadingSvg from "./icons/loading.svg";
import tableIcon from "./icons/table.png";
import urlIcon from "./icons/url.png";
import otherIcon from "./icons/other.png";

/**
 * format the string
 * @param {string} str The origin string
 * @param {number} maxWidth max width
 * @param {number} fontSize font size
 * @return {string} the processed result
 */
const fittingString2 = (str, maxWidth, fontSize) => {
  const ellipsis = '...';
  const ellipsisLength = G6.Util.getTextSize(ellipsis, fontSize)[0];
  let currentWidth = 0;
  let res = str;
  const pattern = new RegExp('[\u4E00-\u9FA5]+'); // distinguish the Chinese charactors and letters
  str.split('').forEach((letter, i) => {
    if (currentWidth > maxWidth - ellipsisLength) return;
    if (pattern.test(letter)) {
      // Chinese charactors
      currentWidth += fontSize;
    } else {
      // get the width of single letter according to the fontSize
      currentWidth += G6.Util.getLetterWidth(letter, fontSize);
    }
    if (currentWidth > maxWidth - ellipsisLength) {
      res = `${str.substr(0, i)}${ellipsis}`;
    }
  });
  return res;
};

// 设置组件的长度和宽度
let size = [180, 34]
const width = parseInt(size[0]);
const height = parseInt(size[1]);
const offsetX = -width / 2
const offsetY = -height / 2
/**
 * format the string
 * @param {string} str The origin string
 * @param {number} maxWidth max width
 * @param {number} fontSize font size
 * @return {string} the processed result
 */
const fittingString = (str, maxWidth, fontSize) => {
  let currentWidth = 0;
  let res = str;
  const pattern = new RegExp('[\u4E00-\u9FA5]+'); // distinguish the Chinese charactors and letters
  str.split('').forEach((letter, i) => {
    if (currentWidth > maxWidth) return;
    if (pattern.test(letter)) {
      // Chinese charactors
      currentWidth += fontSize;
    } else {
      // get the width of single letter according to the fontSize
      currentWidth += G6.Util.getLetterWidth(letter, fontSize);
    }
    if (currentWidth > maxWidth) {
      let second = `${str.substr(i)}`
      res = `${str.substr(0, i)}\n` + fittingString2(second, maxWidth, fontSize);
    }
  });
  return res;
};
const customNode = {
  init() {
    G6.registerNode("customNode", {
      draw(cfg, group) {

        const color = cfg.color;
        const nodeType = cfg.nodeType


        let iconType = urlIcon


        if (nodeType == 1) {
          iconType = urlIcon
        } else if (nodeType == 0) {
          iconType = otherIcon
        } else if (nodeType == 2) {
          iconType = tableIcon
        }


        const mainId = cfg.nodeId
        const shape = group.addShape("rect", {
          attrs: {
            id: mainId,
            x: offsetX,
            y: offsetY,
            width: width,
            height: height,
            fill: '#fff',
            shadowColor: 'rgba(0, 0, 0, 0.08)',
            shadowOffsetX: -4,
            shadowOffsetY: 4,
            shadowBlur: 20,
            // opacity: 0.5,
            radius: 4
          }
        });

        group.addShape("rect", {
          attrs: {
            x: offsetX,
            y: offsetY,
            width: 32,
            height: height,
            // fill: "#1890FF",
            // fill: "#E9F1FF",
            parent: mainId,
            radius: [4, 0, 0, 4]
          }
        });

        group.addShape("image", {
          attrs: {
            x: offsetX + 5,
            y: offsetY + 7,
            width: 20,
            height: 20,
            img: iconType,
            opacity: 1

          },
          draggable: true,

        });


        group.addShape("text", {
          attrs: {
            id: 'nodeName_' + uniqueId(),
            x: offsetX + 40,
            y: 1,
            textAlign: "left",
            textBaseline: "middle",
            text: fittingString(cfg.nodeName, 110, 12),
            fontSize: 12,
            fill: "#565758",
            fontFamily: "Microsoft Yahei"
          },
          draggable: true,

        });


        let inPoints = [[0, 0.5]]
        for (let i = 0; i < inPoints.length; i++) {
          let x,
            y = 0;
          //0为顶 1为底
          if (inPoints[i][0] === 0) {
            y = 0;
          } else {
            y = height;
          }
          x = width * inPoints[i][1];
          const id = 'circle' + uniqueId()
          group.addShape("circle", {
            attrs: {
              id: 'circle' + uniqueId(),
              parent: id,
              x: x + offsetX,
              y: y + offsetY,
              r: 10,
              isInPointOut: true,
              fill: "#1890ff",
              opacity: 0
            }
          });
          group.addShape("circle", {
            attrs: {
              id: id,
              x: x + offsetX,
              y: y + offsetY,
              r: 3,
              isInPoint: true,
              fill: "#fff",
              stroke: "#1890ff",
              opacity: 0
            }
          });
        }

        let outPoints = [[1, 0.5]]

        for (let i = 0; i < outPoints.length; i++) {
          let x,
            y = 0;
          //0为顶 1为底
          if (outPoints[i][0] === 0) {
            y = 0;
          } else {
            y = height;
          }
          x = width * outPoints[i][1];
          const id = 'circle' + uniqueId()
          group.addShape("circle", {
            attrs: {
              id: 'circle' + uniqueId(),
              parent: id,
              x: x + offsetX,
              y: y + offsetY,
              r: 10,
              isOutPointOut: true,
              fill: "#1890ff",
              opacity: 0//默認0 需要時改成0.3
            }
          });
          group.addShape("circle", {
            attrs: {
              id: id,
              x: x + offsetX,
              y: y + offsetY,
              r: 3,
              isOutPoint: true,
              fill: "#fff",
              stroke: "#1890ff",
              opacity: 0
            }
          });
        }

        return shape;
      },
      //设置状态
      setState(name, value, item) {
        const group = item.getContainer();
        const shape = group.get("children")[0]; // 顺序根据 draw 时确定

        const littleRect = group.get("children")[1]; // 顺序根据 draw 时确定


        const children = group.findAll(g => {
          return g.attrs.parent === shape.attrs.id
        });


        const circles = group.findAll(circle => {
          return circle.attrs.isInPoint || circle.attrs.isOutPoint;
        });
        const selectStyles = () => {
          shape.attr("fill", "rgba(243, 249, 255, 0.92)");
          shape.attr("cursor", "move");
          // 设置阴影
          shape.attr("shadowColor", "rgba(64, 169, 255, 0.2)");
          shape.attr("shadowOffsetX", -3);
          shape.attr("shadowOffsetY", 3);
          shape.attr("opacity", "0.5");

          shape.attr("stroke", "#1890ff");
          littleRect.attr("height", height - 2)
          littleRect.attr("x", offsetX + 1)
          littleRect.attr("y", offsetY + 1)


          children.forEach(child => {
            child.attr("cursor", "move");
          });
          circles.forEach(circle => {
            circle.attr('opacity', 1)
          })
        };
        const unSelectStyles = () => {

          shape.attr("fill", "#fff");
          shape.attr("stroke", "rgba(0, 0, 0, 0)");
          shape.attr("shadowColor", "rgba(0, 0, 0, 0.08)");
          shape.attr("shadowOffsetX", -4);
          shape.attr("shadowOffsetY", 4);
          shape.attr("opacity", "1");


          littleRect.attr("height", height)
          littleRect.attr("x", offsetX)
          littleRect.attr("y", offsetY)

          circles.forEach(circle => {
            circle.attr('opacity', 0)
          })
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
      },
      afterDraw(cfg, group) {
        const offsetX = 70
        const nodeState = cfg.nodeState
        const nodeType = cfg.nodeType

        let statusIcon = null
        if (nodeState == 1 && nodeType == 0) {
          statusIcon = loadingSvg
        } else if (nodeState == 1 && nodeType == 1) {
          statusIcon = okSvg
        }
        // 为状态图标添加一个父节点
        const stateImage = group.addShape("image", {
          attrs: {
            x: -9 + offsetX,
            y: -9,
            width: 18,
            height: 18,
            img: statusIcon
          }
        });


        if (nodeState == 1 && nodeType == 0) {
          stateImage.animate(
            {
              // 动画重复
              repeat: true,
              // 每一帧的操作，入参 ratio：这一帧的比例值（Number）。返回值：这一帧需要变化的参数集（Object）。
              onFrame(ratio) {
                // 旋转通过矩阵来实现
                // 当前矩阵

                // 目标矩阵
                // const toMatrix = G6.Util.transform(matrix, [
                //   ['r', ratio * Math.PI * 2],
                // ]);
                const toMatrix = G6.Util.transform([1, 0, 0, 0, 1, 0, 0, 0, 1],
                  [['t', -offsetX, 0],
                    ['r', ratio * Math.PI * 2],
                    ['t', +offsetX, 0]]);
                // 返回这一帧需要的参数集，本例中只有目标矩阵
                return {
                  matrix: toMatrix,
                };
              },
            },
            1200,
            'easeLinear',
          );
        }


      }
    });
  }
}

export default customNode
