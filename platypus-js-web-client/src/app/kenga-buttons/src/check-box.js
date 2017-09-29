define([
    'core/extend',
    './radio-button'], function (
        extend,
        RadioButton) {
    function CheckBox(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = false;
        if (arguments.length < 1)
            text = '';

        RadioButton.call(this, text, selected, onActionPerformed);
        
        var box = this.element.firstElementChild;
        box.type = 'checkbox';
    }
    extend(CheckBox, RadioButton);
    return CheckBox;
});
       