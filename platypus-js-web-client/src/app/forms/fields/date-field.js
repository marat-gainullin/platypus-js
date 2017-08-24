define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function DateField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'date';
    }
    extend(DateField, TextValueField);
    return DateField;
});
