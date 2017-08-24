define([
    '../../ui',
    '../../extend',
    './button'], function (
        Ui,
        extend,
        Button) {
    function DropDownButton(text, icon, iconTextGap, onActionPerformed) {
        if (arguments.length < 3)
            iconTextGap = 4;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        Button.call(this, text, icon, iconTextGap, onActionPerformed);
        var self = this;

        var dropDown = document.createElement('div');
        dropDown.classList.add('p-dropdown-chevron');
        this.element.appendChild(dropDown);

        var dropDownMenu;
        Object.defineProperty(this, "dropDownMenu", {
            get: function () {
                return dropDownMenu;
            },
            set: function (aValue) {
                dropDownMenu = aValue;
            }
        });
    }
    extend(DropDownButton, Button);
    return DropDownButton;
});