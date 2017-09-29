define([
    'core/extend',
    '../fields/formatted-field',
    'ui/bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelFormattedField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelFormattedField, Field);
    return ModelFormattedField;
});