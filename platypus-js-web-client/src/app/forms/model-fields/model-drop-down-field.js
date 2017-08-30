define([
    '../../extend',
    '../fields/drop-down-field',
    '../bound',
    '../decorator'
], function (
        extend,
        Field,
        Bound,
        Decorator) {
    function ModelDropDownField() {
        Field.call(this, document.createElement('div'));
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelDropDownField, Field);
    return ModelDropDownField;
});