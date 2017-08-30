define([
    '../../extend',
    '../fields/password-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelPasswordField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelPasswordField, Field);
    return ModelPasswordField;
});