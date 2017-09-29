define([
    'core/extend',
    '../fields/date-time-field',
    'ui/bound',
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