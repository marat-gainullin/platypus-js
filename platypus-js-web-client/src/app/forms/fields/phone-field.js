define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function PhoneField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'tel';
    }
    extend(PhoneField, TextValueField);
    return PhoneField;
});
