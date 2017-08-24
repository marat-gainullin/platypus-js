define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function ColorField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'color';
    }
    extend(ColorField, TextValueField);
    return ColorField;
});
