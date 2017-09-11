define([
    '../../../../extend',
    '../node',
    '../../columns/radio-button-service-column',
    '../node-view'], function(
        extend,
        HeaderNode,
        RadioServiceColumn,
        HeaderView){
    function RadioServiceHeaderNode(){
        var column = new RadioServiceColumn();
        var header = new HeaderView("\\", this);
        HeaderNode.call(this, column, header);
        
        function lightCopy() {
            var copied = new RadioServiceHeaderNode();
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
    extend(RadioServiceHeaderNode, HeaderNode);
    return RadioServiceHeaderNode;
});
