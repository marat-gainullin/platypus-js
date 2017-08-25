define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function PasswordField() {
        TextField.call(this);
        var self = this;

        var box = this.element;
        box.type = 'password';
    }
    extend(PasswordField, TextField);
    return PasswordField;
});
