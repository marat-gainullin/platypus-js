define([
    'core/extend',
    './image-paragraph'], function(
        extend,
        ImageParagraph){
    function Label(text, icon, iconTextGap) {
        if (arguments.length < 3)
            iconTextGap = 4;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        ImageParagraph.call(this, document.createElement('div'), text, icon, iconTextGap);
        var self = this;
        this.opaque = false;
    }
    extend(Label, ImageParagraph);
    return Label;
});