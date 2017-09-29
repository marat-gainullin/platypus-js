define([
    'core/extend',
    '../column-node',
    '../../columns/check-box-service-column',
    '../../header/node-view'], function(
        extend,
        HeaderNode,
        CheckServiceColumn,
        NodeView){
    function CheckServiceColumnNode(){
        var self = this;
        var column = new CheckServiceColumn(this);
        var header = new NodeView('\\', this);
        HeaderNode.call(this, column, header);
        column.editor = null;
        
        function copy() {
            var copied = new CheckServiceColumnNode();
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
    extend(CheckServiceColumnNode, HeaderNode);
    return CheckServiceColumnNode;
});
