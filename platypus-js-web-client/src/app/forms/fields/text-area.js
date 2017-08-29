define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function TextArea(shell) {
        var box = document.createElement('textarea');
        if(!shell)
            shell = box;
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(TextArea, TextField);
    return TextArea;
});
