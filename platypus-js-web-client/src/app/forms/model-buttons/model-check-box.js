define([
    '../../extend',
    '../buttons/check-box',
    '../bound'], function (
        extend,
        CheckBox,
        Bound) {
    function ModelCheckBox(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = false;
        if (arguments.length < 1)
            text = '';
        
        CheckBox.call(this, text, selected, onActionPerformed);
        Bound.call(this);
    }
    extend(ModelCheckBox, CheckBox);
    return ModelCheckBox;
});