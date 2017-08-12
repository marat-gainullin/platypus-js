define(['./image-paragraph'], function(ImageParagraph){
    function DropDownButton(text, icon, iconTextGap, onActionPerformed) {
        if (arguments.length < 3)
            iconTextGap = 4;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        ImageParagraph.call(this, document.createElement('button'), text, icon, iconTextGap);
        var image = this.element.firstElementChild;
        var paragraph = this.element.lastElementChild;
        var self = this;
        this.opaque = true;
        this.onActionPerformed = onActionPerformed;
        
        var dropDown = document.createElement('div');
        dropDown.classList.add('p-dropdown-chevron');
        
        var dropDownMenu;
        Object.defineProperty(this, "dropDownMenu", {
            get: function() {
                return dropDownMenu;
            },
            set: function(aValue) {
                dropDownMenu = aValue;
            }
        });
    }
    extend(DropDownButton, ImageParagraph);
    return DropDownButton;
});