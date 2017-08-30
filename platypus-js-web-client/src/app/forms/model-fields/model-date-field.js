define([
    '../../extend',
    '../fields/date-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelDateField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelDateField, Field);
    return ModelDateField;
});