define(['./image-paragraph'], function(ImageParagraph){
    function Button(text, icon, iconTextGap, onActionPerformed) {
        if (arguments.length < 3)
            iconTextGap = 4;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        ImageParagraph.call(this, document.createElement('button'), text, icon, iconTextGap);
        var self = this;
        this.opaque = true;
        this.onActionPerformed = onActionPerformed;
    }
    extend(Button, ImageParagraph);
    return Button;
});