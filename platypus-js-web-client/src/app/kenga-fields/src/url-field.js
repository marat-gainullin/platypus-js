define([
    'core/extend',
    './text-field'], function (
        extend,
        TextField) {
    function UrlField(shell) {
        var box = document.createElement('input');
        box.type = 'url';
        if(!shell)
            shell = box;
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(UrlField, TextField);
    return UrlField;
});
