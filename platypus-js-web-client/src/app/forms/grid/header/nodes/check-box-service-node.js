define([
    '../../../../extend',
    '../node',
    '../../columns/check-box-service-column',
    '../node-view'], function(
        extend,
        HeaderNode,
        CheckServiceColumn,
        HeaderView){
    function CheckServiceHeaderNode(){
        var column = new CheckServiceColumn();
        var header = new HeaderView("\\", this);
        HeaderNode.call(this, column, header);
        
        function lightCopy() {
            var copied = new CheckServiceHeaderNode();
            return copied;
        }

        Object.defineProperty(this, 'lightCopy', {
            get: function () {
                return lightCopy;
            }
        });
        Object.defineProperty(this, 'column', {
            get: function () {
                return column;
            }
        });
        Object.defineProperty(this, 'header', {
            get: function () {
                return header;
            }
        });
        Object.defineProperty(this, 'editor', {
            get: function () {
                return null;
            }
        });
        
        this.resizable = false;
    }
    extend(CheckServiceHeaderNode, HeaderNode);
    return CheckServiceHeaderNode;
});
