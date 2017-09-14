define([
    '../../../../extend',
    '../column-node',
    '../radio-button-service-column',
    '../../header/node-view'], function(
        extend,
        HeaderNode,
        RadioServiceColumn,
        NodeView){
    function RadioServiceColumnNode(){
        var self = this;
        var column = new RadioServiceColumn();
        var header = new NodeView('\\', this);
        HeaderNode.call(this, column, header);
        
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
