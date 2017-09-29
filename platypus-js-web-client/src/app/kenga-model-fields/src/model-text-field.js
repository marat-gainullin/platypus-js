define([
    'core/extend',
    '../fields/text-field',
    'ui/bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelTextField(text) {
        if (arguments.length < 1)
            text = '';
        Field.call(this, text, null, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelTextField, Field);
    return ModelTextField;
});