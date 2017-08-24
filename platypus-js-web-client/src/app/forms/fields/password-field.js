define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function PasswordField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'password';
    }
    extend(PasswordField, TextValueField);
    return PasswordField;
});
