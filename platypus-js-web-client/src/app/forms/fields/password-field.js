define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function PasswordField(shell) {
        var box = document.createElement('input');
        box.type = 'password';
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(PasswordField, TextField);
    return PasswordField;
});
