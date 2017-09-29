define([
    'core/extend',
    './text-field'], function (
        extend,
        TextField) {
    function PhoneField(shell) {
        var box = document.createElement('input');
        box.type = 'tel';
        if(!shell)
            shell = box;
        
        TextField.call(this, '', box, shell);
        var self = this;
    }
    extend(PhoneField, TextField);
    return PhoneField;
});
