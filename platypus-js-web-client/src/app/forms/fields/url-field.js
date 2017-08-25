define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function UrlField() {
        TextField.call(this);
        var self = this;

        var box = this.element;
        box.type = 'url';
    }
    extend(UrlField, TextField);
    return UrlField;
});
