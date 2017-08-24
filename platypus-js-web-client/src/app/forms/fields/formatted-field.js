define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function FormattedField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
    }
    extend(FormattedField, TextValueField);
    return FormattedField;
});
