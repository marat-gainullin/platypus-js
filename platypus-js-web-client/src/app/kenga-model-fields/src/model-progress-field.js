define([
    'core/extend',
    '../fields/progress-field',
    'ui/bound'
], function (
        extend,
        Field,
        Bound) {
    function ModelProgressField() {
        Field.call(this, null, document.createElement('div'));
        Bound.call(this);
    }
    extend(ModelProgressField, Field);
    return ModelProgressField;
});