import bus from '@/plugins/bus';

export default {
  getDefaultCfg() {
    return {
      multiple: true,
      keyCode: 16
    };
  },
  getEvents() {
    return {
      'node:click': 'onClick',
      'canvas:click': 'onCanvasClick',
      'canvas:mouseover': 'onCanvasMouseover',
      keyup: 'onKeyUp',
      keydown: 'onKeyDown'
    };
  },
  onClick(e) {
    const self = this;
    const item = e.item;
    const graph = self.graph;
    const autoPaint = graph.get('autoPaint');
    graph.setAutoPaint(false);
    const selectedEdges = graph.findAllByState('edge', 'selected');
    selectedEdges.forEach(edge => {
      graph.setItemState(edge, 'selected', false);
    });
    if (!self.keydown || !self.multiple) {
      const selected = graph.findAllByState('node', 'selected');
      selected.forEach(node => {
        if (node !== item) {
          graph.setItemState(node, 'selected', false);
        }
      });
    }
    if (item.hasState('selected')) {
      if (self.shouldUpdate.call(self, e)) {
        graph.setItemState(item, 'selected', false);
      }

      bus.$emit('nodeselectchange', {target: item, select: false});
    } else {
      if (self.shouldUpdate.call(self, e)) {
        graph.setItemState(item, 'selected', true);
      }
      bus.$emit('nodeselectchange', {target: item, select: true});
    }
    graph.setAutoPaint(autoPaint);
    graph.paint();
  },
  onCanvasClick() {
    const graph = this.graph;
    const autoPaint = graph.get('autoPaint');
    graph.setAutoPaint(false);
    const selected = graph.findAllByState('node', 'selected');
    selected.forEach(node => {
      graph.setItemState(node, 'selected', false);
      bus.$emit('nodeselectchange', {target: node, select: false});
    });

    const selectedEdges = graph.findAllByState('edge', 'selected');
    selectedEdges.forEach(edge => {
      graph.setItemState(edge, 'selected', false);
      bus.$emit('nodeselectchange', {target: edge, select: false});
    })

    graph.paint();
    graph.setAutoPaint(autoPaint);
  },
  onCanvasMouseover() {
    const graph = this.graph;
    graph.paint();
  },
  onKeyDown(e) {
    const code = e.keyCode || e.which;
    if (code === this.keyCode) {
      this.keydown = true;
    } else {
      this.keydown = false;
    }
  },
  onKeyUp() {
    this.keydown = false;
  }
};
