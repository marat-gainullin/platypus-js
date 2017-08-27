define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function PhoneField(shell) {
        var box = document.createElement('input');
        box.type = 'tel';
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(PhoneField, TextField);
    return PhoneField;
});
