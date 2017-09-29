define([
    'core/extend',
    '../column-node',
    '../radio-button-service-column',
    '../../header/node-view'], function(
        extend,
        HeaderNode,
        RadioServiceColumn,
        NodeView){
    function RadioServiceColumnNode(){
        var self = this;
        var column = new RadioServiceColumn(this);
        var header = new NodeView('\\', this);
        HeaderNode.call(this, column, header);
        column.editor = null; // TODO: Add radio button default editor
        
        function copy() {
            var copied = new RadioServiceColumnNode();
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
    extend(RadioServiceColumnNode, HeaderNode);
    return RadioServiceColumnNode;
});
