define(['./image-paragraph'], function(ImageParagraph){
    function ToggleButton(text, icon, selected, iconTextGap, onActionPerformed) {
        if (arguments.length < 4)
            iconTextGap = 4;
        if (arguments.length < 3)
            selected = false;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        ImageParagraph.call(this, document.createElement('button'), text, icon, iconTextGap);
        var self = this;
        this.opaque = true;
        this.onActionPerformed = onActionPerformed;
    }
    extend(ToggleButton, ImageParagraph);
    return ToggleButton;
});