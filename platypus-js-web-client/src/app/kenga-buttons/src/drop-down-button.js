define([
    'ui/utils',
    'core/extend',
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
        var mouseDownReg;
        var mouseClickReg;
        Object.defineProperty(this, "dropDownMenu", {
            get: function () {
                return dropDownMenu;
            },
            set: function (aValue) {
                if (dropDownMenu !== aValue) {
                    if (mouseDownReg) {
                        mouseDownReg.removeHandler();
                        mouseDownReg = null;
                    }
                    if (mouseClickReg) {
                        mouseClickReg.removeHandler();
                        mouseClickReg = null;
                    }
                    dropDownMenu = aValue;
                    if (dropDownMenu) {
                        mouseDownReg = Ui.on(dropDown, Ui.Events.MOUSEDOWN, function (evt) {
                            evt.stopPropagation();
                            Ui.startMenuSession(dropDownMenu);
                            dropDownMenu.showRelativeTo(dropDown, false);
                        }, false);
                        mouseClickReg = Ui.on(dropDown, Ui.Events.CLICK, function (evt) {
                            evt.stopPropagation();
                        }, false);
                    }
                }
            }
        });
    }
    extend(DropDownButton, Button);
    return DropDownButton;
});