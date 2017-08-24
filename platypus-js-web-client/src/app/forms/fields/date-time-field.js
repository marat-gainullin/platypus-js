define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function DateTimeField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'datetime-local';
    }
    extend(DateTimeField, TextValueField);
    return DateTimeField;
});
