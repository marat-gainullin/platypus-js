define([
    '../../extend',
    '../fields/progress-field',
    '../bound'
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