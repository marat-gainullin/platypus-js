define([
    'core/extend',
    '../column-node',
    '../order-num-service-column',
    '../../header/node-view'], function(
        extend,
        ColumnNode,
        OrderNumServiceColumn,
        NodeView){
    function OrderNumServiceColumnNode(){
        var self = this;
        var column = new OrderNumServiceColumn(this);
        var header = new NodeView('\\', this);
        ColumnNode.call(this, column, header);
        
        function copy() {
            var copied = new OrderNumServiceColumnNode();
            copied.column = column;
            copied.view.text = header.text;
            copied.leavesCount = self.leavesCount;
            copied.depthRemainder = self.depthRemainder;
            return copied;
        }

        Object.defineProperty(this, 'copy', {
            get: function () {
                return copy;
            }
        });
        Object.defineProperty(this, 'renderer', {
            get: function () {
                return null;
            }
        });
        Object.defineProperty(this, 'editor', {
            get: function () {
                return null;
            }
        });
        
        this.resizable = false;
    }
    extend(OrderNumServiceColumnNode, ColumnNode);
    return OrderNumServiceColumnNode;
});
