define([
    '../../extend',
    './text-field'], function (
        extend,
        TextField) {
    function EMailField() {
        TextField.call(this);
        var self = this;

        var box = this.element;
        box.type = 'email';        
    }
    extend(EMailField, TextField);
    return EMailField;
});
