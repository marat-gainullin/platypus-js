define([
    '../../extend',
    './text-value-field'], function (
        extend,
        TextValueField) {
    function NumberField() {
        TextValueField.call(this);

        var self = this;

        var box = this.element;
        box.type = 'number';
        
        Object.defineProperty(this, 'min', {
            get: function(){
                return parseFloat(box.min);
            },
            set: function(aValue){
                box.min = aValue;
            }
        });
        Object.defineProperty(this, 'max', {
            get: function(){
                return parseFloat(box.max);
            },
            set: function(aValue){
                box.max = aValue;
            }
        });
        Object.defineProperty(this, 'step', {
            get: function(){
                return parseFloat(box.step);
            },
            set: function(aValue){
                box.step = aValue;
            }
        });
    }
    extend(NumberField, TextValueField);
    return NumberField;
});
