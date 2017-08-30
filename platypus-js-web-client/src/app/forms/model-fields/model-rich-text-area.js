define([
    '../../extend',
    '../fields/rich-text-area',
    '../bound',
    '../decorator'
], function (
        extend,
        Area,
        Bound,
        Decorator) {
    function ModelRichTextArea(text) {
        if (arguments.length < 1)
            text = '';
        Area.call(this, text);
        Bound.call(this);
        Decorator.call(this);
    }
    extend(ModelRichTextArea, Area);
    return ModelRichTextArea;
});