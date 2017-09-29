define([
    'core/extend',
    './text-field'], function (
        extend,
        TextField) {
    function EMailField(shell) {
        var box = document.createElement('input');
        box.type = 'email';
        if(!shell)
            shell = box;
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(EMailField, TextField);
    return EMailField;
});
