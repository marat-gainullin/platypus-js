define([
    '../../extend',
    '../fields/phone-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelPhoneField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelPhoneField, Field);
    return ModelPhoneField;
});