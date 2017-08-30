define([
    '../../extend',
    '../fields/email-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelEMailField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelEMailField, Field);
    return ModelEMailField;
});