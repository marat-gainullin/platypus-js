define([
    '../../../../extend',
    '../header-node',
    '../../columns/usual-service-column',
    '../node-view'], function(
        extend,
        HeaderNode,
        UsualServiceColumn,
        HeaderView){
    function UsualServiceHeaderNode(){
        var column = new UsualServiceColumn();
        var header = new HeaderView("\\", this);
        HeaderNode.call(this, column, header);
        
        function lightCopy() {
            var copied = new UsualServiceHeaderNode();
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
    extend(UsualServiceHeaderNode, HeaderNode);
    return UsualServiceHeaderNode;
});
