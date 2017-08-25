define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function PhoneField() {
        TextField.call(this);
        var self = this;

        var box = this.element;
        box.type = 'tel';
    }
    extend(PhoneField, TextField);
    return PhoneField;
});
