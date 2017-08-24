define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function EMailField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'email';
    }
    extend(EMailField, TextValueField);
    return EMailField;
});
