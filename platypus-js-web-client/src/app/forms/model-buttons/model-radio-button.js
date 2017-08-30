define([
    '../../extend',
    '../buttons/radio-button',
    '../bound'], function (
        extend,
        CheckBox,
        Bound) {
    function ModelRadioButton(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = false;
        if (arguments.length < 1)
            text = '';
        
        CheckBox.call(this, text, selected, onActionPerformed);
        Bound.call(this);
    }
    extend(ModelRadioButton, CheckBox);
    return ModelRadioButton;
});