define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function UrlField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'url';
    }
    extend(UrlField, TextValueField);
    return UrlField;
});
