define([
    '../../extend',
    '../fields/number-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelNumberField() {
        Field.call(this, null, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelNumberField, Field);
    return ModelNumberField;
});