define([
    'core/extend',
    '../buttons/radio-button',
    'ui/bound'], function (
        extend,
        RadioButton,
        Bound) {
    function ModelRadioButton(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = null;
        if (arguments.length < 1)
            text = '';
        
        RadioButton.call(this, text, selected, onActionPerformed);
        Bound.call(this);
    }
    extend(ModelRadioButton, RadioButton);
    return ModelRadioButton;
});