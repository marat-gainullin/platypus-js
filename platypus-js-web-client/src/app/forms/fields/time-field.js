define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function TimeField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'time';
    }
    extend(TimeField, TextValueField);
    return TimeField;
});
