define([
    '../../extend',
    '../fields/meter-field',
    '../bound'
], function (
        extend,
        Field,
        Bound) {
    function ModelMeterField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
    }
    extend(ModelMeterField, Field);
    return ModelMeterField;
});