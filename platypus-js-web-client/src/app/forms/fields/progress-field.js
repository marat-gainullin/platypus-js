define([
    '../../extend',
    './number-field'], function (
        extend,
        NumberField) {
    function ProgressField(shell) {
        var box = document.createElement('progress');
        
        NumberField.call(this, box, shell);
        var self = this;
    }
    extend(ProgressField, NumberField);
    return ProgressField;
});
