define([
    'core/extend',
    '../fields/slider',
    'ui/bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelSliderField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelSliderField, Field);
    return ModelSliderField;
});