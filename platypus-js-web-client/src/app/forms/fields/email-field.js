define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function EMailField(shell) {
        var box = document.createElement('input');
        box.type = 'email';
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(EMailField, TextField);
    return EMailField;
});
