define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function TextArea(shell) {
        var box = document.createElement('textarea');
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(TextArea, TextField);
    return TextArea;
});
