define([
    '../../extend',
    '../fields/url-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelUrlField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelUrlField, Field);
    return ModelUrlField;
});