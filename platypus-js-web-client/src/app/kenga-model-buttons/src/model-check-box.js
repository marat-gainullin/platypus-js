define([
    'core/extend',
    '../buttons/check-box',
    'ui/bound'], function (
        extend,
        CheckBox,
        Bound) {
    function ModelCheckBox(text, selected, onActionPerformed) {
        if (arguments.length < 2)
            selected = null;
        if (arguments.length < 1)
            text = '';
        
        CheckBox.call(this, text, selected, onActionPerformed);
        Bound.call(this);
    }
    extend(ModelCheckBox, CheckBox);
    return ModelCheckBox;
});