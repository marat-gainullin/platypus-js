define([
    'core/extend',
    '../column-node',
    '../marker-service-column',
    '../../header/node-view'], function(
        extend,
        HeaderNode,
        MarkerServiceColumn,
        NodeView){
    function MarkerServiceColumnNode(){
        var self = this;
        var column = new MarkerServiceColumn(this);
        var header = new NodeView('\\', this);
        HeaderNode.call(this, column, header);
        
        function copy() {
            var copied = new MarkerServiceColumnNode();
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
    extend(MarkerServiceColumnNode, HeaderNode);
    return MarkerServiceColumnNode;
});
