define([
    'core/extend',
    '../fields/time-field',
    'ui/bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelTimeField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelTimeField, Field);
    return ModelTimeField;
});