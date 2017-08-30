define([
    '../../extend',
    '../fields/date-time-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelDateTimeField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelDateTimeField, Field);
    return ModelDateTimeField;
});