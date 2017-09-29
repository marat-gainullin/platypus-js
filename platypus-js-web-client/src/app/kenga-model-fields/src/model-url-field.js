define([
    'core/extend',
    '../fields/url-field',
    'ui/bound',
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