define([
    '../../extend',
    '../buttons/toggle-button',
    '../bound'], function (
        extend,
        ToggleButton,
        Bound) {
    function ModelCheckBox(text, icon, selected, iconTextGap, onActionPerformed) {
        if (arguments.length < 4)
            iconTextGap = 4;
        if (arguments.length < 3)
            selected = false;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        
        ToggleButton.call(this, text, icon, selected, iconTextGap, onActionPerformed);
        Bound.call(this);
    }
    extend(ModelCheckBox, ToggleButton);
    return ModelCheckBox;
});