define([
    '../../extend',
    './number-field'], function (
        extend,
        NumberField) {
    function RangeField() {
        NumberField.call(this);
        var self = this;

        var box = this.element;
        box.type = 'range';
    }
    extend(RangeField, NumberField);
    return RangeField;
});
