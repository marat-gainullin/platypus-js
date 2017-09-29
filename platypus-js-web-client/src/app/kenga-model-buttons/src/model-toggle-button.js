define([
    'core/extend',
    '../buttons/toggle-button',
    'ui/bound'], function (
        extend,
        ToggleButton,
        Bound) {
    function ModelToggleButton(text, icon, selected, iconTextGap, onActionPerformed) {
        if (arguments.length < 4)
            iconTextGap = 4;
        if (arguments.length < 3)
            selected = null;
        if (arguments.length < 2)
            icon = null;
        if (arguments.length < 1)
            text = '';
        
        ToggleButton.call(this, text, icon, selected, iconTextGap, onActionPerformed);
        Bound.call(this);
    }
    extend(ModelToggleButton, ToggleButton);
    return ModelToggleButton;
});